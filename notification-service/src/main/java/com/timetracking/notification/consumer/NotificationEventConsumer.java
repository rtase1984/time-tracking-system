package com.timetracking.notification.notification_service.event.consumer;

import com.timetracking.notification.notification_service.domain.EmailNotification;
import com.timetracking.notification.notification_service.service.EmailService;
import com.timetracking.timesheet.event.TimeEntryRegisteredEvent;
import com.timetracking.timesheet.event.TimesheetApprovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationEventConsumer {

  private final EmailService emailService;

  @KafkaListener(topics = "time.entry.registered", groupId = "notification-service")
  public void consumeTimeEntryRegistered(TimeEntryRegisteredEvent event) {
    log.info("Processing time entry notification: {}", event.getPayload().getEntryId());

    // In production, fetch user email from user service
    String email = "user@example.com"; // Placeholder

    emailService.sendTimeEntryNotification(
        event.getPayload().getUserId(),
        email,
        event.getPayload().getEntryType().toString(),
        event.getPayload().getEntryTimestamp()
    );
  }

  @KafkaListener(topics = "timesheet.approved", groupId = "notification-service")
  public void consumeTimesheetApproved(TimesheetApprovedEvent event) {
    log.info("Processing timesheet approved notification: {}", event.getTimesheetId());

    String email = "user@example.com"; // Placeholder

    emailService.sendTimesheetApprovedNotification(
        event.getUserId(),
        email,
        event.getPeriodMonth(),
        event.getPeriodYear()
    );
  }

  @KafkaListener(topics = "timesheet.rejected", groupId = "notification-service")
  public void consumeTimesheetRejected(TimesheetRejectedEvent event) {
    log.info("Processing timesheet rejected notification: {}", event.getTimesheetId());

    String email = "user@example.com"; // Placeholder

    Map<String, Object> variables = new HashMap<>();
    variables.put("month", event.getPeriodMonth());
    variables.put("year", event.getPeriodYear());
    variables.put("status", "REJECTED");
    variables.put("reason", event.getReason());

    EmailNotification notification = EmailNotification.builder()
        .to(email)
        .subject("Timesheet Rejected")
        .templateName("timesheet-status")
        .variables(variables)
        .build();

    emailService.sendEmail(notification);
  }

  @KafkaListener(topics = "invoice.generated", groupId = "notification-service")
  public void consumeInvoiceGenerated(InvoiceGeneratedEvent event) {
    log.info("Processing invoice generated notification: {}", event.getInvoiceNumber());

    String email = "user@example.com"; // Placeholder

    emailService.sendInvoiceGeneratedNotification(
        event.getUserId(),
        email,
        event.getInvoiceNumber(),
        event.getTotal()
    );
  }
}
