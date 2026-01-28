package com.timetracking.timesheet.event.consumer;

import com.timetracking.timesheet.event.TimeEntryRegisteredEvent;
import com.timetracking.timesheet.service.TimesheetService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TimeEntryConsumer {

  private final TimesheetService timesheetService;

  @KafkaListener(topics = "time.entry.registered", groupId = "timesheet-service")
  public void consumeTimeEntry(TimeEntryRegisteredEvent event) {
    log.info("Received time entry event: {}", event.getPayload().getEntryId());

    // Calculate hours and update timesheet
    // This is simplified - in production, aggregate all entries for the month
    UUID userId = event.getPayload().getUserId();
    LocalDateTime timestamp = event.getPayload().getEntryTimestamp();

    int month = timestamp.getMonthValue();
    int year = timestamp.getYear();

    // This would normally calculate based on all entries
    BigDecimal hours = BigDecimal.valueOf(8); // Placeholder

    timesheetService.createOrUpdateTimesheet(userId, month, year, hours);
  }
}
