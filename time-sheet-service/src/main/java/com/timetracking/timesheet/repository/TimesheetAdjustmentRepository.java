package com.timetracking.timesheet.repository;

import com.timetracking.timesheet.domain.entity.TimesheetAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TimesheetAdjustmentRepository extends JpaRepository<TimesheetAdjustment, UUID> {
}
