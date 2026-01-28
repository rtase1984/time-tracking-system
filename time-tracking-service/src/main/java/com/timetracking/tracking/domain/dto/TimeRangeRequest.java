package com.timetracking.tracking.domain.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeRangeRequest {

  @NotNull(message = "Start date is required")
  private LocalDate startDate;

  @NotNull(message = "End date is required")
  private LocalDate endDate;
}
