package com.timetracking.notification.controller;

import com.timetracking.notification.notification_service.EmailService;
import com.timetracking.notification.domain.NotificationRequest;
import com.timetracking.notification.domain.EmailNotification;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications")
@Slf4j
public class NotificationController {

  private final EmailService emailService;

  @PostMapping("/send")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Map<String, String>> sendNotification(
      @RequestBody NotificationRequest request) {

    log.info("Manual notification request received: type={}, user={}",
        request.getType(), request.getUserId());

    switch (request.getType()) {
      case TIME_ENTRY_REGISTERED:
        emailService.sendTimeEntryNotification(
            request.getUserId(),
            request.getUserEmail(),
            (String) request.getData().get("entryType"),
            LocalDateTime.parse((String) request.getData().get("timestamp")));
        break;
      case TIMESHEET_APPROVED:
        emailService.sendTimesheetApprovedNotification(
            request.getUserId(),
            request.getUserEmail(),
            (Integer) request.getData().get("month"),
            (Integer) request.getData().get("year"));
        break;
      case INVOICE_GENERATED:
        emailService.sendInvoiceGeneratedNotification(
            request.getUserId(),
            request.getUserEmail(),
            (String) request.getData().get("invoiceNumber"),
            new BigDecimal(request.getData().get("total").toString()));
        break;
      case REMINDER_CHECK_IN:
      case REMINDER_CHECK_OUT:
        emailService.sendReminderNotification(
            request.getUserId(),
            request.getUserEmail(),
            request.getType().name());
        break;
      default:
        log.warn("Unhandled notification type: {}", request.getType());
        return ResponseEntity.badRequest().body(Map.of("error", "Unhandled notification type"));
    }

    return ResponseEntity.ok(Map.of("status", "sent"));
  }

  @PostMapping("/test")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Map<String, String>> sendTestEmail(
      @RequestParam String to) {

    EmailNotification notification = EmailNotification.builder()
        .to(to)
        .subject("Test Email")
        .templateName("test")
        .variables(Map.of("message", "This is a test email"))
        .build();

    emailService.sendEmail(notification);

    return ResponseEntity.ok(Map.of("status", "sent", "to", to));
  }
}
