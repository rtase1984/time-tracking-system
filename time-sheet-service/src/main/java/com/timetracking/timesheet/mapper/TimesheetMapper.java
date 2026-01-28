package com.timetracking.timesheet.mapper;

import com.timetracking.timesheet.domain.dto.TimesheetResponse;
import com.timetracking.timesheet.domain.entity.Timesheet;
import org.springframework.stereotype.Component;

@Component
public class TimesheetMapper {

    public TimesheetResponse toResponse(Timesheet timesheet) {
        if (timesheet == null) {
            return null;
        }

        return TimesheetResponse.builder()
                .id(timesheet.getId())
                .userId(timesheet.getUserId())
                .periodMonth(timesheet.getPeriodMonth())
                .periodYear(timesheet.getPeriodYear())
                .totalHours(timesheet.getTotalHours())
                .status(timesheet.getStatus())
                .approvedBy(timesheet.getApprovedBy())
                .approvedAt(timesheet.getApprovedAt())
                .adjustments(timesheet.getAdjustments().stream()
                        .map(this::toAdjustmentResponse)
                        .collect(java.util.stream.Collectors.toList()))
                .createdAt(timesheet.getCreatedAt())
                .build();
    }

    private com.timetracking.timesheet.domain.dto.AdjustmentResponse toAdjustmentResponse(
            com.timetracking.timesheet.domain.entity.TimesheetAdjustment adjustment) {
        return com.timetracking.timesheet.domain.dto.AdjustmentResponse.builder()
                .id(adjustment.getId())
                .adjustedBy(adjustment.getAdjustedBy())
                .adjustmentHours(adjustment.getAdjustmentHours())
                .reason(adjustment.getReason())
                .createdAt(adjustment.getCreatedAt())
                .build();
    }
}
