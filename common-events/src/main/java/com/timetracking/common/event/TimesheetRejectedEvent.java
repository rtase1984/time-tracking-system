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
public class TimesheetRejectedEvent {
    private EventPayload payload;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventPayload {
        private UUID eventId;
        private UUID timesheetId;
        private UUID userId;
        private Integer periodMonth;
        private Integer periodYear;
        private String reason;
        private LocalDateTime rejectedAt;
    }
}
