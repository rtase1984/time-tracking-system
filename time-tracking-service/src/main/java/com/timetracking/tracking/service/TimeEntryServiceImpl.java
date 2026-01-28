package com.timetracking.tracking.service;

import com.timetracking.tracking.domain.dto.DailySummaryResponse;
import com.timetracking.tracking.domain.dto.TimeEntryRequest;
import com.timetracking.tracking.domain.dto.TimeEntryResponse;
import com.timetracking.tracking.domain.entity.EntryType;
import com.timetracking.tracking.domain.entity.TimeEntry;
import com.timetracking.tracking.event.publiser.TimeEntryEventPublisher;
import com.timetracking.tracking.exception.BusinessException;
import com.timetracking.tracking.repository.TimeEntryRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeEntryServiceImpl implements TimeEntryService {

  private final TimeEntryRepository timeEntryRepository;
  private final TimeEntryEventPublisher eventPublisher;

  @Value("${time-tracking.max-entries-per-day:10}")
  private int maxEntriesPerDay;

  @Value("${time-tracking.allow-past-entries-days:7}")
  private int allowPastEntriesDays;

  @Override
  @Transactional
  @CacheEvict(value = "userEntries", key = "#result.userId")
  public TimeEntryResponse createEntry(TimeEntryRequest request, String ipAddress, String userAgent) {
    UUID userId = getCurrentUserId();
    LocalDateTime timestamp = request.getEntryTimestamp() != null
        ? request.getEntryTimestamp()
        : LocalDateTime.now();

    // Validations
    validateTimestamp(timestamp);
    validateMaxEntriesPerDay(userId, timestamp);
    validateEntrySequence(userId, request.getEntryType(), timestamp);

    TimeEntry entry = TimeEntry.builder()
        .userId(userId)
        .entryType(request.getEntryType())
        .entryTimestamp(timestamp)
        .location(request.getLocation())
        .ipAddress(ipAddress)
        .deviceInfo(userAgent)
        .notes(request.getNotes())
        .build();

    entry = timeEntryRepository.save(entry);
    log.info("Time entry created: userId={}, type={}, timestamp={}",
        userId, entry.getEntryType(), entry.getEntryTimestamp());

    // Publish event to Kafka
    eventPublisher.publishTimeEntryRegistered(entry);

    return mapToResponse(entry);
  }

  @Override
  @Transactional(readOnly = true)
  public TimeEntryResponse getEntryById(UUID entryId) {
    TimeEntry entry = timeEntryRepository.findById(entryId)
        .orElseThrow(() -> new BusinessException("Time entry not found"));

    validateUserAccess(entry.getUserId());
    return mapToResponse(entry);
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "userEntries", key = "#userId")
  public List<TimeEntryResponse> getUserEntries(UUID userId) {
    validateUserAccess(userId);
    return timeEntryRepository.findByUserIdOrderByEntryTimestampDesc(userId)
        .stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<TimeEntryResponse> getUserEntriesByDateRange(UUID userId, LocalDate startDate, LocalDate endDate) {
    validateUserAccess(userId);

    LocalDateTime start = startDate.atStartOfDay();
    LocalDateTime end = endDate.atTime(LocalTime.MAX);

    return timeEntryRepository.findByUserIdAndEntryTimestampBetweenOrderByEntryTimestampAsc(
            userId, start, end)
        .stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "dailySummary", key = "#userId + '-' + #date")
  public DailySummaryResponse getDailySummary(UUID userId, LocalDate date) {
    validateUserAccess(userId);

    LocalDateTime dateTime = date.atStartOfDay();
    List<TimeEntry> entries = timeEntryRepository.findByUserIdAndDate(userId, dateTime);

    if (entries.isEmpty()) {
      return DailySummaryResponse.builder()
          .date(date)
          .totalMinutesWorked(0L)
          .breakMinutes(0L)
          .formattedTotalTime("0h 0m")
          .entries(Collections.emptyList())
          .isComplete(false)
          .build();
    }

    return calculateDailySummary(date, entries);
  }

  @Override
  @Transactional(readOnly = true)
  public List<DailySummaryResponse> getWeeklySummary(UUID userId, LocalDate weekStart) {
    validateUserAccess(userId);

    List<DailySummaryResponse> weeklySummary = new ArrayList<>();
    for (int i = 0; i < 7; i++) {
      LocalDate date = weekStart.plusDays(i);
      weeklySummary.add(getDailySummary(userId, date));
    }
    return weeklySummary;
  }

  @Override
  @Transactional
  @CacheEvict(value = {"userEntries", "dailySummary"}, allEntries = true)
  public void deleteEntry(UUID entryId) {
    TimeEntry entry = timeEntryRepository.findById(entryId)
        .orElseThrow(() -> new BusinessException("Time entry not found"));

    validateUserAccess(entry.getUserId());
    timeEntryRepository.delete(entry);
    log.info("Time entry deleted: id={}, userId={}", entryId, entry.getUserId());
  }

  // Private helper methods

  private void validateTimestamp(LocalDateTime timestamp) {
    if (timestamp.isAfter(LocalDateTime.now())) {
      throw new BusinessException("Cannot create entry in the future");
    }

    LocalDateTime oldestAllowed = LocalDateTime.now().minusDays(allowPastEntriesDays);
    if (timestamp.isBefore(oldestAllowed)) {
      throw new BusinessException("Cannot create entry older than " + allowPastEntriesDays + " days");
    }
  }

  private void validateMaxEntriesPerDay(UUID userId, LocalDateTime timestamp) {
    long count = timeEntryRepository.countEntriesByUserIdAndDate(userId, timestamp);
    if (count >= maxEntriesPerDay) {
      throw new BusinessException("Maximum entries per day exceeded");
    }
  }

  private void validateEntrySequence(UUID userId, EntryType entryType, LocalDateTime timestamp) {
    // Business logic: CHECK_OUT must follow CHECK_IN, etc.
    Optional<TimeEntry> lastEntry = timeEntryRepository.findLastEntryByUserIdAndType(
        userId, EntryType.CHECK_IN);

    if (entryType == EntryType.CHECK_OUT && lastEntry.isEmpty()) {
      throw new BusinessException("Cannot check out without checking in first");
    }
  }

  private DailySummaryResponse calculateDailySummary(LocalDate date, List<TimeEntry> entries) {
    LocalDateTime firstCheckIn = null;
    LocalDateTime lastCheckOut = null;
    long totalMinutes = 0L;
    long breakMinutes = 0L;

    LocalDateTime currentCheckIn = null;
    LocalDateTime currentBreakStart = null;

    for (TimeEntry entry : entries) {
      switch (entry.getEntryType()) {
        case CHECK_IN:
          if (firstCheckIn == null) {
            firstCheckIn = entry.getEntryTimestamp();
          }
          currentCheckIn = entry.getEntryTimestamp();
          break;

        case CHECK_OUT:
          lastCheckOut = entry.getEntryTimestamp();
          if (currentCheckIn != null) {
            totalMinutes += Duration.between(currentCheckIn, lastCheckOut).toMinutes();
            currentCheckIn = null;
          }
          break;

        case BREAK_START:
          currentBreakStart = entry.getEntryTimestamp();
          break;

        case BREAK_END:
          if (currentBreakStart != null) {
            breakMinutes += Duration.between(currentBreakStart, entry.getEntryTimestamp()).toMinutes();
            currentBreakStart = null;
          }
          break;
      }
    }

    // Subtract break time from total
    long netMinutes = Math.max(0, totalMinutes - breakMinutes);
    String formattedTime = formatMinutes(netMinutes);

    boolean isComplete = firstCheckIn != null && lastCheckOut != null;

    return DailySummaryResponse.builder()
        .date(date)
        .firstCheckIn(firstCheckIn)
        .lastCheckOut(lastCheckOut)
        .totalMinutesWorked(netMinutes)
        .breakMinutes(breakMinutes)
        .formattedTotalTime(formattedTime)
        .entries(entries.stream().map(this::mapToResponse).collect(Collectors.toList()))
        .isComplete(isComplete)
        .build();
  }

  private String formatMinutes(long minutes) {
    long hours = minutes / 60;
    long mins = minutes % 60;
    return hours + "h " + mins + "m";
  }

  private UUID getCurrentUserId() {
    // This would normally extract from JWT token in SecurityContext
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    // For now, we'll need to implement a way to get userId from email
    // This is a simplified version - in production, you'd query the auth service
    return UUID.randomUUID(); // Placeholder
  }

  private void validateUserAccess(UUID userId) {
    UUID currentUserId = getCurrentUserId();
    // Allow if same user or if user is ADMIN/SUPERVISOR
    // This is simplified - implement proper authorization
  }

  private TimeEntryResponse mapToResponse(TimeEntry entry) {
    return TimeEntryResponse.builder()
        .id(entry.getId())
        .userId(entry.getUserId())
        .entryType(entry.getEntryType())
        .entryTimestamp(entry.getEntryTimestamp())
        .location(entry.getLocation())
        .ipAddress(entry.getIpAddress())
        .deviceInfo(entry.getDeviceInfo())
        .notes(entry.getNotes())
        .createdAt(entry.getCreatedAt())
        .build();
  }
}
