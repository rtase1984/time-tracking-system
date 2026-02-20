package com.timetracking.notification.notification_service.domain;

import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationRequest {
  private UUID userId;
  private String userEmail;
  private NotificationType type;
  private Map<String, Object> data;
}