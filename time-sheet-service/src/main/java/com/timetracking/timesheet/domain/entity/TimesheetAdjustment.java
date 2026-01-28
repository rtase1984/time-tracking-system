package com.timetracking.timesheet.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "timesheet_adjustments")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class TimesheetAdjustment {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "timesheet_id", nullable = false)
  private Timesheet timesheet;

  @Column(name = "adjusted_by", nullable = false)
  private UUID adjustedBy;

  @Column(name = "adjustment_hours", precision = 10, scale = 2)
  private BigDecimal adjustmentHours;

  @Column(columnDefinition = "TEXT")
  private String reason;

  @Column(name = "created_at")
  private LocalDateTime createdAt;
}