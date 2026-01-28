package com.timetracking.timesheet.controller;

import com.timetracking.timesheet.domain.dto.AdjustmentRequest;
import com.timetracking.timesheet.domain.dto.ApprovalRequest;
import com.timetracking.timesheet.domain.dto.TimesheetResponse;
import com.timetracking.timesheet.service.TimesheetService;
import com.timetracking.timesheet.util.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/timesheets")
@RequiredArgsConstructor
@Tag(name = "Timesheets")
public class TimesheetController {

  private final TimesheetService timesheetService;

  @GetMapping("/user/{userId}")
  @PreAuthorize("hasAnyRole('WORKER', 'SUPERVISOR', 'ADMIN')")
  public ResponseEntity<List<TimesheetResponse>> getUserTimesheets(@PathVariable UUID userId) {
    return ResponseEntity.ok(timesheetService.getUserTimesheets(userId));
  }

  @GetMapping("/{timesheetId}")
  @PreAuthorize("hasAnyRole('WORKER', 'SUPERVISOR', 'ADMIN')")
  public ResponseEntity<TimesheetResponse> getTimesheet(@PathVariable UUID timesheetId) {
    return ResponseEntity.ok(timesheetService.getTimesheetById(timesheetId));
  }

  @PostMapping("/{timesheetId}/approve")
  @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
  public ResponseEntity<TimesheetResponse> approveTimesheet(
      @PathVariable UUID timesheetId,
      @RequestBody ApprovalRequest request) {
    UUID supervisorId = SecurityUtils.getCurrentUserId();
    return ResponseEntity.ok(timesheetService.approveTimesheet(timesheetId, request, supervisorId));
  }

  @PostMapping("/{timesheetId}/adjustments")
  @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
  public ResponseEntity<TimesheetResponse> addAdjustment(
      @PathVariable UUID timesheetId,
      @RequestBody AdjustmentRequest request) {
    UUID supervisorId = SecurityUtils.getCurrentUserId();
    return ResponseEntity.ok(timesheetService.addAdjustment(timesheetId, request, supervisorId));
  }
}
