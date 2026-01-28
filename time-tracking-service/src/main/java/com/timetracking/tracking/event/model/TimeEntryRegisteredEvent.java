package com.timetracking.tracking.event.model;

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
public class TimeEntryRegisteredEvent {
  private UUID eventId;
  private String eventType;
  private LocalDateTime timestamp;
  private TimeEntryPayload payload;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TimeEntryPayload {
    private UUID entryId;
    private UUID userId;
    private EntryType entryType;
    private LocalDateTime entryTimestamp;
    private String location;
  }
}