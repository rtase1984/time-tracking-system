package com.timetracking.tracking.controller;

import com.timetracking.tracking.domain.dto.DailySummaryResponse;
import com.timetracking.tracking.domain.dto.TimeEntryRequest;
import com.timetracking.tracking.domain.dto.TimeEntryResponse;
import com.timetracking.tracking.service.TimeEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Time Tracking", description = "Time entry management endpoints")
@RestController
@RequestMapping("/api/v1/time-entries")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TimeEntryController {

  private final TimeEntryService timeEntryService;

  @Operation(summary = "Create time entry", description = "Register check-in, check-out, or break")
  @PostMapping
  @PreAuthorize("hasAnyRole('WORKER', 'SUPERVISOR', 'ADMIN')")
  public ResponseEntity<TimeEntryResponse> createEntry(
      @Valid @RequestBody TimeEntryRequest request,
      HttpServletRequest httpRequest) {

    String ipAddress = getClientIp(httpRequest);
    String userAgent = httpRequest.getHeader("User-Agent");

    TimeEntryResponse response = timeEntryService.createEntry(request, ipAddress, userAgent);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(summary = "Get entry by ID")
  @GetMapping("/{entryId}")
  @PreAuthorize("hasAnyRole('WORKER', 'SUPERVISOR', 'ADMIN')")
  public ResponseEntity<TimeEntryResponse> getEntry(@PathVariable UUID entryId) {
    TimeEntryResponse response = timeEntryService.getEntryById(entryId);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Get user entries", description = "Get all entries for a specific user")
  @GetMapping("/user/{userId}")
  @PreAuthorize("hasAnyRole('WORKER', 'SUPERVISOR', 'ADMIN')")
  public ResponseEntity<List<TimeEntryResponse>> getUserEntries(@PathVariable UUID userId) {
    List<TimeEntryResponse> entries = timeEntryService.getUserEntries(userId);
    return ResponseEntity.ok(entries);
  }

  @Operation(summary = "Get entries by date range")
  @GetMapping("/user/{userId}/range")
  @PreAuthorize("hasAnyRole('WORKER', 'SUPERVISOR', 'ADMIN')")
  public ResponseEntity<List<TimeEntryResponse>> getUserEntriesByRange(
      @PathVariable UUID userId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

    List<TimeEntryResponse> entries = timeEntryService.getUserEntriesByDateRange(
        userId, startDate, endDate);
    return ResponseEntity.ok(entries);
  }

  @Operation(summary = "Get daily summary", description = "Get summary for a specific day")
  @GetMapping("/user/{userId}/daily/{date}")
  @PreAuthorize("hasAnyRole('WORKER', 'SUPERVISOR', 'ADMIN')")
  public ResponseEntity<DailySummaryResponse> getDailySummary(
      @PathVariable UUID userId,
      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

    DailySummaryResponse summary = timeEntryService.getDailySummary(userId, date);
    return ResponseEntity.ok(summary);
  }

  @Operation(summary = "Get weekly summary", description = "Get summary for a week starting from date")
  @GetMapping("/user/{userId}/weekly/{weekStart}")
  @PreAuthorize("hasAnyRole('WORKER', 'SUPERVISOR', 'ADMIN')")
  public ResponseEntity<List<DailySummaryResponse>> getWeeklySummary(
      @PathVariable UUID userId,
      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {

    List<DailySummaryResponse> summary = timeEntryService.getWeeklySummary(userId, weekStart);
    return ResponseEntity.ok(summary);
  }

  @Operation(summary = "Delete entry")
  @DeleteMapping("/{entryId}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public ResponseEntity<Void> deleteEntry(@PathVariable UUID entryId) {
    timeEntryService.deleteEntry(entryId);
    return ResponseEntity.noContent().build();
  }

  private String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("X-Real-IP");
    }
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }
}
