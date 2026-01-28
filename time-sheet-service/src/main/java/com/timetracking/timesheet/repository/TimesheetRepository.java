package com.timetracking.timesheet.repository;

import com.timetracking.timesheet.domain.entity.Timesheet;
import com.timetracking.timesheet.domain.entity.TimesheetStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TimesheetRepository extends JpaRepository<Timesheet, UUID> {
  Optional<Timesheet> findByUserIdAndPeriodMonthAndPeriodYear(
      UUID userId, Integer month, Integer year);

  List<Timesheet> findByUserIdOrderByPeriodYearDescPeriodMonthDesc(UUID userId);

  List<Timesheet> findByStatusAndPeriodMonthAndPeriodYear(
      TimesheetStatus status, Integer month, Integer year);

  @Query("SELECT t FROM Timesheet t WHERE t.userId IN :userIds " +
      "AND t.status = :status")
  List<Timesheet> findByUserIdsAndStatus(
      @Param("userIds") List<UUID> userIds,
      @Param("status") TimesheetStatus status);

  List<Timesheet> findByPeriodMonthAndPeriodYear(int month, int year);

}

