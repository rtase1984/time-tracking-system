package com.timetracking.timesheet.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "timesheets")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Timesheet {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "period_month", nullable = false)
  private Integer periodMonth;

  @Column(name = "period_year", nullable = false)
  private Integer periodYear;

  @Column(name = "total_hours", precision = 10, scale = 2)
  private BigDecimal totalHours;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TimesheetStatus status;

  @Column(name = "approved_by")
  private UUID approvedBy;

  @Column(name = "approved_at")
  private LocalDateTime approvedAt;

  @Builder.Default
  @OneToMany(mappedBy = "timesheet", cascade = CascadeType.ALL)
  private List<TimesheetAdjustment> adjustments = new ArrayList<>();

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
