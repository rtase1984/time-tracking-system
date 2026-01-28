package com.timetracking.timesheet.job;

import com.timetracking.timesheet.service.TimesheetService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class TimesheetCalculationJob {

  private final TimesheetService timesheetService;

  // Run monthly on the 1st at 00:00
  @Scheduled(cron = "0 0 0 1 * ?")
  public void calculateMonthlyTimesheets() {
    log.info("Starting monthly timesheet calculation");

    LocalDate previousMonth = LocalDate.now().minusMonths(1);
    int month = previousMonth.getMonthValue();
    int year = previousMonth.getYear();

    timesheetService.calculateAllTimesheetsForPeriod(month, year);

    log.info("Monthly timesheet calculation completed");
  }
}
