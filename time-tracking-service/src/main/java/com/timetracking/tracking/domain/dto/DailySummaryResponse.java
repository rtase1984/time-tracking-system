package com.timetracking.tracking.domain.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailySummaryResponse {
  private LocalDate date;
  private LocalDateTime firstCheckIn;
  private LocalDateTime lastCheckOut;
  private Long totalMinutesWorked;
  private Long breakMinutes;
  private String formattedTotalTime;
  private List<TimeEntryResponse> entries;
  private boolean isComplete;
}