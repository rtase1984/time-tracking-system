package com.timetracking.common.event;

import java.math.BigDecimal;
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
public class TimesheetApprovedEvent {
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
        private BigDecimal totalHours;
        private LocalDateTime approvedAt;
    }
}
