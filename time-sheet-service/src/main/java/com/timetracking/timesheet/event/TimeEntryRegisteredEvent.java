package com.timetracking.timesheet.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeEntryRegisteredEvent {
    private EventPayload payload;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventPayload {
        private UUID entryId;
        private UUID userId;
        private LocalDateTime entryTimestamp;
        private Double durationHours;
    }
}
