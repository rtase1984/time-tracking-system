package com.timetracking.timesheet.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdjustmentResponse {
  private UUID id;
  private UUID adjustedBy;
  private BigDecimal adjustmentHours;
  private String reason;
  private LocalDateTime createdAt;
}
