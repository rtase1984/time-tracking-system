package com.timetracking.billing.domain.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class InvoiceGenerationRequest {
  @NotNull
  private UUID timesheetId;
  @NotNull
  private UUID userId;
  @NotNull
  private Integer month;
  @NotNull
  private Integer year;
  @NotNull
  private BigDecimal totalHours;
  @NotNull
  private BigDecimal ratePerHour;
  private BigDecimal taxRate = BigDecimal.valueOf(0.10);
}
