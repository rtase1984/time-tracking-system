package com.timetracking.timesheet.service;

import com.timetracking.timesheet.domain.dto.AdjustmentRequest;
import com.timetracking.timesheet.domain.dto.ApprovalRequest;
import com.timetracking.timesheet.domain.dto.TimesheetResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TimesheetService {
  List<TimesheetResponse> getUserTimesheets(UUID userId);

  TimesheetResponse getTimesheetById(UUID timesheetId);

  TimesheetResponse createOrUpdateTimesheet(UUID userId, int month, int year, BigDecimal hours);

  TimesheetResponse approveTimesheet(UUID timesheetId, ApprovalRequest request, UUID supervisorId);

  TimesheetResponse addAdjustment(UUID timesheetId, AdjustmentRequest request, UUID supervisorId);

  void calculateAllTimesheetsForPeriod(int year, int month);
}
