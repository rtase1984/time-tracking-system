package com.timetracking.tracking.repository;

import com.timetracking.tracking.domain.entity.EntryType;
import com.timetracking.tracking.domain.entity.TimeEntry;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntry, UUID> {

  List<TimeEntry> findByUserIdOrderByEntryTimestampDesc(UUID userId);

  List<TimeEntry> findByUserIdAndEntryTimestampBetweenOrderByEntryTimestampAsc(
      UUID userId, LocalDateTime start, LocalDateTime end);

  @Query("SELECT te FROM TimeEntry te WHERE te.userId = :userId " +
      "AND DATE(te.entryTimestamp) = DATE(:date) " +
      "ORDER BY te.entryTimestamp ASC")
  List<TimeEntry> findByUserIdAndDate(@Param("userId") UUID userId,
      @Param("date") LocalDateTime date);

  @Query("SELECT te FROM TimeEntry te WHERE te.userId = :userId " +
      "AND te.entryType = :entryType " +
      "ORDER BY te.entryTimestamp DESC")
  Optional<TimeEntry> findLastEntryByUserIdAndType(@Param("userId") UUID userId,
      @Param("entryType") EntryType entryType);

  @Query("SELECT COUNT(te) FROM TimeEntry te WHERE te.userId = :userId " +
      "AND DATE(te.entryTimestamp) = DATE(:date)")
  long countEntriesByUserIdAndDate(@Param("userId") UUID userId,
      @Param("date") LocalDateTime date);

  boolean existsByUserIdAndEntryTimestamp(UUID userId, LocalDateTime timestamp);
}

