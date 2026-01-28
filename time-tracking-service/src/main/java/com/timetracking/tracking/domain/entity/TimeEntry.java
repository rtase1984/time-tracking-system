package com.timetracking.tracking.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "time_entries")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Enumerated(EnumType.STRING)
  @Column(name = "entry_type", nullable = false, length = 20)
  private EntryType entryType;

  @Column(name = "entry_timestamp", nullable = false)
  private LocalDateTime entryTimestamp;

  @Column(length = 255)
  private String location;

  @Column(name = "ip_address", length = 50)
  private String ipAddress;

  @Column(name = "device_info", length = 255)
  private String deviceInfo;

  @Column(columnDefinition = "TEXT")
  private String notes;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
