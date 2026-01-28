package com.timetracking.timesheet.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimesheetApprovedEvent {
    private UUID eventId;
    private UUID timesheetId;
    private UUID userId;
    private Integer periodMonth;
    private Integer periodYear;
    private BigDecimal totalHours;
    private LocalDateTime approvedAt;
}
