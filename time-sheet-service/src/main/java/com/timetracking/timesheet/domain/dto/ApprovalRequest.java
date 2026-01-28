package com.timetracking.timesheet.domain.dto;

import com.timetracking.timesheet.domain.entity.TimesheetStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApprovalRequest {
  @NotNull
  private TimesheetStatus status; // APPROVED or REJECTED
  private String comments;
}
