package com.timetracking.tracking.domain.dto;

import com.timetracking.tracking.domain.entity.EntryType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeEntryRequest {

  @NotNull(message = "Entry type is required")
  private EntryType entryType;

  @PastOrPresent(message = "Entry timestamp cannot be in the future")
  private LocalDateTime entryTimestamp;

  private String location;
  private String notes;
}