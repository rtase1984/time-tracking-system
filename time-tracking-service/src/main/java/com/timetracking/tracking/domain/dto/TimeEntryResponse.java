package com.timetracking.tracking.domain.dto;

import com.timetracking.tracking.domain.entity.EntryType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeEntryResponse {
  private UUID id;
  private UUID userId;
  private EntryType entryType;
  private LocalDateTime entryTimestamp;
  private String location;
  private String ipAddress;
  private String deviceInfo;
  private String notes;
  private LocalDateTime createdAt;
}
