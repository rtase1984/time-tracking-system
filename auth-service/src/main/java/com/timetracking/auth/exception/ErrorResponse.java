package com.timetracking.auth.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
  private LocalDateTime timestamp;
  private int status;
  private String error;
  private String message;
  private String path;
  private List<ValidationError> validationErrors;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ValidationError {
    private String field;
    private String message;
  }
}