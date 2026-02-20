package com.timetracking.notification.notification_service.service;

import com.timetracking.notification.notification_service.domain.EmailNotification;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  @Value("${notification.from-email}")
  private String fromEmail;

  @Value("${notification.from-name}")
  private String fromName;

  @Async
  @Override
  public void sendEmail(EmailNotification notification) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom(new InternetAddress(fromEmail, fromName));
      helper.setTo(notification.getTo());
      helper.setSubject(notification.getSubject());

      // Process template
      Context context = new Context();
      context.setVariables(notification.getVariables());
      String htmlContent = templateEngine.process(notification.getTemplateName(), context);

      helper.setText(htmlContent, true);

      if (notification.getCc() != null && !notification.getCc().isEmpty()) {
        helper.setCc(notification.getCc().toArray(new String[0]));
      }

      mailSender.send(message);
      log.info("Email sent successfully to: {}", notification.getTo());

    } catch (Exception e) {
      log.error("Failed to send email to: {}", notification.getTo(), e);
      throw new NotificationException("Failed to send email", e);
    }
  }

  @Override
  public void sendTimeEntryNotification(UUID userId, String email, String entryType, LocalDateTime timestamp) {
    Map<String, Object> variables = new HashMap<>();
    variables.put("entryType", entryType);
    variables.put("timestamp", timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

    EmailNotification notification = EmailNotification.builder()
        .to(email)
        .subject("Time Entry Registered - " + entryType)
        .templateName("time-entry")
        .variables(variables)
        .build();

    sendEmail(notification);
  }

  @Override
  public void sendTimesheetApprovedNotification(UUID userId, String email, Integer month, Integer year) {
    Map<String, Object> variables = new HashMap<>();
    variables.put("month", getMonthName(month));
    variables.put("year", year);
    variables.put("status", "APPROVED");

    EmailNotification notification = EmailNotification.builder()
        .to(email)
        .subject("Timesheet Approved - " + getMonthName(month) + " " + year)
        .templateName("timesheet-status")
        .variables(variables)
        .build();

    sendEmail(notification);
  }

  @Override
  public void sendInvoiceGeneratedNotification(UUID userId, String email, String invoiceNumber, BigDecimal total) {
    Map<String, Object> variables = new HashMap<>();
    variables.put("invoiceNumber", invoiceNumber);
    variables.put("total", total);
    variables.put("downloadUrl", "http://localhost:8084/api/v1/invoices/" + invoiceNumber + "/pdf");

    EmailNotification notification = EmailNotification.builder()
        .to(email)
        .subject("Invoice Generated - " + invoiceNumber)
        .templateName("invoice-generated")
        .variables(variables)
        .build();

    sendEmail(notification);
  }

  @Override
  public void sendReminderNotification(UUID userId, String email, String reminderType) {
    Map<String, Object> variables = new HashMap<>();
    variables.put("reminderType", reminderType);
    variables.put("currentTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

    EmailNotification notification = EmailNotification.builder()
        .to(email)
        .subject("Reminder: " + reminderType)
        .templateName("reminder")
        .variables(variables)
        .build();

    sendEmail(notification);
  }

  private String getMonthName(Integer month) {
    return LocalDate.of(2024, month, 1).getMonth().toString();
  }
}
