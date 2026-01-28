package com.timetracking.timesheet.service;

import com.timetracking.timesheet.domain.dto.AdjustmentRequest;
import com.timetracking.timesheet.domain.dto.ApprovalRequest;
import com.timetracking.timesheet.domain.dto.TimesheetResponse;
import com.timetracking.timesheet.domain.entity.Timesheet;
import com.timetracking.timesheet.domain.entity.TimesheetAdjustment;
import com.timetracking.timesheet.domain.entity.TimesheetStatus;
import com.timetracking.timesheet.event.TimesheetApprovedEvent;
import com.timetracking.timesheet.exception.BusinessException;
import com.timetracking.timesheet.mapper.TimesheetMapper;
import com.timetracking.timesheet.repository.TimesheetRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimesheetServiceImpl implements TimesheetService {

  private final TimesheetRepository timesheetRepository;
  private final TimesheetMapper timesheetMapper;
  private final KafkaTemplate<String, Object> kafkaTemplate;

  @Override
  @Transactional(readOnly = true)
  public List<TimesheetResponse> getUserTimesheets(UUID userId) {
    return timesheetRepository.findByUserIdOrderByPeriodYearDescPeriodMonthDesc(userId)
        .stream()
        .map(timesheetMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public TimesheetResponse getTimesheetById(UUID timesheetId) {
    return timesheetRepository.findById(timesheetId)
        .map(timesheetMapper::toResponse)
        .orElseThrow(() -> new BusinessException("Timesheet not found"));
  }

  @Override
  @Transactional
  public TimesheetResponse createOrUpdateTimesheet(UUID userId, int month, int year, BigDecimal hours) {
    Timesheet timesheet = timesheetRepository
        .findByUserIdAndPeriodMonthAndPeriodYear(userId, month, year)
        .orElse(Timesheet.builder()
            .userId(userId)
            .periodMonth(month)
            .periodYear(year)
            .status(TimesheetStatus.DRAFT)
            .createdAt(LocalDateTime.now())
            .build());

    timesheet.setTotalHours(hours);
    timesheet.setUpdatedAt(LocalDateTime.now());

    timesheet = timesheetRepository.save(timesheet);
    log.info("Timesheet updated: userId={}, period={}/{}, hours={}",
        userId, month, year, hours);

    return timesheetMapper.toResponse(timesheet);
  }

  @Override
  @Transactional
  public TimesheetResponse approveTimesheet(UUID timesheetId, ApprovalRequest request, UUID supervisorId) {
    Timesheet timesheet = timesheetRepository.findById(timesheetId)
        .orElseThrow(() -> new BusinessException("Timesheet not found"));

    if (timesheet.getStatus() != TimesheetStatus.SUBMITTED && timesheet.getStatus() != TimesheetStatus.DRAFT) {
      log.warn("Timesheet status is {}, but allowing approval/rejection for simulation", timesheet.getStatus());
    }

    timesheet.setStatus(request.getStatus());
    timesheet.setApprovedBy(supervisorId);
    timesheet.setApprovedAt(LocalDateTime.now());
    timesheet.setUpdatedAt(LocalDateTime.now());

    timesheet = timesheetRepository.save(timesheet);

    // Publish event to Kafka
    if (request.getStatus() == TimesheetStatus.APPROVED) {
      publishTimesheetApproved(timesheet);
    }

    return timesheetMapper.toResponse(timesheet);
  }

  @Override
  @Transactional
  public TimesheetResponse addAdjustment(UUID timesheetId, AdjustmentRequest request, UUID supervisorId) {
    Timesheet timesheet = timesheetRepository.findById(timesheetId)
        .orElseThrow(() -> new BusinessException("Timesheet not found"));

    TimesheetAdjustment adjustment = TimesheetAdjustment.builder()
        .timesheet(timesheet)
        .adjustedBy(supervisorId)
        .adjustmentHours(request.getHours())
        .reason(request.getReason())
        .createdAt(LocalDateTime.now())
        .build();

    timesheet.getAdjustments().add(adjustment);

    // Recalculate total hours
    BigDecimal totalAdjustments = timesheet.getAdjustments().stream()
        .map(TimesheetAdjustment::getAdjustmentHours)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    // Note: totalHours could be null if not initialized
    BigDecimal baseHours = timesheet.getTotalHours() != null ? timesheet.getTotalHours() : BigDecimal.ZERO;
    timesheet.setTotalHours(baseHours.add(request.getHours())); // Adding ONLY the new adjustment to keep it simple

    timesheet = timesheetRepository.save(timesheet);

    return timesheetMapper.toResponse(timesheet);
  }

  private void publishTimesheetApproved(Timesheet timesheet) {
    TimesheetApprovedEvent event = TimesheetApprovedEvent.builder()
        .eventId(UUID.randomUUID())
        .timesheetId(timesheet.getId())
        .userId(timesheet.getUserId())
        .periodMonth(timesheet.getPeriodMonth())
        .periodYear(timesheet.getPeriodYear())
        .totalHours(timesheet.getTotalHours())
        .approvedAt(timesheet.getApprovedAt())
        .build();

    kafkaTemplate.send("timesheet.approved", timesheet.getUserId().toString(), event);
    log.info("Timesheet approved event published: {}", timesheet.getId());
  }

  @Override
  @Transactional
  public void calculateAllTimesheetsForPeriod(int month, int year) {
    log.info("Calculating all timesheets for period {}/{}", month, year);

    List<Timesheet> timesheets =
        timesheetRepository.findByPeriodMonthAndPeriodYear(month, year);

    if (timesheets.isEmpty()) {
      log.info("No timesheets found for period {}/{}", month, year);
      return;
    }

    for (Timesheet timesheet : timesheets) {

      if (timesheet.getTotalHours() == null) {
        timesheet.setTotalHours(BigDecimal.ZERO);
      }

      if (timesheet.getStatus() == TimesheetStatus.DRAFT) {
        timesheet.setStatus(TimesheetStatus.SUBMITTED);
      }

      timesheet.setUpdatedAt(LocalDateTime.now());

      timesheetRepository.save(timesheet);

      log.debug(
          "Timesheet calculated: id={}, userId={}, hours={}",
          timesheet.getId(),
          timesheet.getUserId(),
          timesheet.getTotalHours()
      );
    }

    log.info(
        "Finished calculating {} timesheets for period {}/{}",
        timesheets.size(),
        month,
        year
    );
  }
}

