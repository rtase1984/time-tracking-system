package com.timetracking.auth.domain.dto;

import com.timetracking.auth.domain.entity.UserRole;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
  private UUID id;
  private String email;
  private String firstName;
  private String lastName;
  private String fullName;
  private UserRole role;
  private UUID supervisorId;
  private String supervisorName;
  private Boolean active;
  private LocalDateTime createdAt;
}
