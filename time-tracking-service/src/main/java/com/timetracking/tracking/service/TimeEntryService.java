package com.timetracking.tracking.service;

import com.timetracking.tracking.domain.dto.DailySummaryResponse;
import com.timetracking.tracking.domain.dto.TimeEntryRequest;
import com.timetracking.tracking.domain.dto.TimeEntryResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TimeEntryService {
  TimeEntryResponse createEntry(TimeEntryRequest request, String ipAddress, String userAgent);
  TimeEntryResponse getEntryById(UUID entryId);
  List<TimeEntryResponse> getUserEntries(UUID userId);
  List<TimeEntryResponse> getUserEntriesByDateRange(UUID userId, LocalDate startDate, LocalDate endDate);
  DailySummaryResponse getDailySummary(UUID userId, LocalDate date);
  List<DailySummaryResponse> getWeeklySummary(UUID userId, LocalDate weekStart);
  void deleteEntry(UUID entryId);
}