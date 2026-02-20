package com.timetracking.notification.notification_service;

import com.timetracking.notification.domain.EmailNotification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface EmailService {
  void sendEmail(EmailNotification notification);
  void sendTimeEntryNotification(UUID userId, String email, String entryType, LocalDateTime timestamp);
  void sendTimesheetApprovedNotification(UUID userId, String email, Integer month, Integer year) ;
  void sendInvoiceGeneratedNotification(UUID userId, String email, String invoiceNumber, BigDecimal total) ;
  void sendReminderNotification(UUID userId, String email, String reminderType);
}
