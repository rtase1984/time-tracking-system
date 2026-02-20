package com.timetracking.common.event;

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
    private EventPayload payload;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventPayload {
        private UUID entryId;
        private UUID userId;
        private EntryType entryType;
        private LocalDateTime entryTimestamp;
        private String location;
    }
}
