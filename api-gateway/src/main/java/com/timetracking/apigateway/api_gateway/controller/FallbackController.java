package com.timetracking.apigateway.api_gateway.controller;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

  @GetMapping("/auth")
  public ResponseEntity<Map<String, String>> authFallback() {
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(Map.of(
            "error", "Auth Service Unavailable",
            "message", "Please try again later"));
  }

  @GetMapping("/time-tracking")
  public ResponseEntity<Map<String, String>> timeTrackingFallback() {
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(Map.of(
            "error", "Time Tracking Service Unavailable",
            "message", "Please try again later"));
  }

  @GetMapping("/timesheet")
  public ResponseEntity<Map<String, String>> timesheetFallback() {
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(Map.of(
            "error", "Timesheet Service Unavailable",
            "message", "Please try again later"));
  }

  @GetMapping("/billing")
  public ResponseEntity<Map<String, String>> billingFallback() {
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(Map.of(
            "error", "Billing Service Unavailable",
            "message", "Please try again later"));
  }

  @GetMapping("/notification")
  public ResponseEntity<Map<String, String>> notificationFallback() {
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(Map.of(
            "error", "Notification Service Unavailable",
            "message", "Please try again later"));
  }
}