package com.timetracking.notification.notification_service.job;

import com.timetracking.notification.notification_service.service.EmailService;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class ReminderJob {

  private final EmailService emailService;

  // Check-in reminder at 9:00 AM every weekday
  @Scheduled(cron = "0 0 9 * * MON-FRI")
  public void sendCheckInReminder() {
    log.info("Sending check-in reminders");

    // In production, query users who haven't checked in
    List<UUID> usersWithoutCheckIn = getUsersWithoutCheckIn();

    for (UUID userId : usersWithoutCheckIn) {
      String email = getUserEmail(userId);
      emailService.sendReminderNotification(userId, email, "Please remember to check in");
    }
  }

  // Check-out reminder at 5:00 PM every weekday
  @Scheduled(cron = "0 0 17 * * MON-FRI")
  public void sendCheckOutReminder() {
    log.info("Sending check-out reminders");

    // In production, query users who haven't checked out
    List<UUID> usersWithoutCheckOut = getUsersWithoutCheckOut();

    for (UUID userId : usersWithoutCheckOut) {
      String email = getUserEmail(userId);
      emailService.sendReminderNotification(userId, email, "Please remember to check out");
    }
  }

  // Timesheet submission reminder on last day of month
  @Scheduled(cron = "0 0 9 L * ?")
  public void sendTimesheetSubmissionReminder() {
    log.info("Sending timesheet submission reminders");

    // Query users with pending timesheets
    List<UUID> usersWithPendingTimesheets = getUsersWithPendingTimesheets();

    for (UUID userId : usersWithPendingTimesheets) {
      String email = getUserEmail(userId);
      emailService.sendReminderNotification(userId, email, "Please submit your timesheet");
    }
  }

  private List<UUID> getUsersWithoutCheckIn() {
    // Placeholder - implement actual logic
    return Collections.emptyList();
  }

  private List<UUID> getUsersWithoutCheckOut() {
    // Placeholder - implement actual logic
    return Collections.emptyList();
  }

  private List<UUID> getUsersWithPendingTimesheets() {
    // Placeholder - implement actual logic
    return Collections.emptyList();
  }

  private String getUserEmail(UUID userId) {
    // Placeholder - fetch from auth service
    return "user@example.com";
  }
}
