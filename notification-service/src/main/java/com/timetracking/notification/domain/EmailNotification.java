package com.timetracking.notification.notification_service.domain;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailNotification {
  private String to;
  private String subject;
  private String templateName;
  private Map<String, Object> variables;
  private List<String> cc;
  private List<String> bcc;
}
