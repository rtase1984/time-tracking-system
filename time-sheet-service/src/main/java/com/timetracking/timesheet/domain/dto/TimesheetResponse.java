package com.timetracking.timesheet.domain.dto;

import com.timetracking.timesheet.domain.entity.TimesheetStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimesheetResponse {

  private UUID id;
  private UUID userId;
  private Integer periodMonth;
  private Integer periodYear;
  private BigDecimal totalHours;
  private TimesheetStatus status;
  private UUID approvedBy;
  private LocalDateTime approvedAt;
  private List<AdjustmentResponse> adjustments;
  private LocalDateTime createdAt;
}
