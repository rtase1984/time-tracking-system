package com.timetracking.auth.exception;

import com.timetracking.auth.exception.ErrorResponse.ValidationError;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(
      BusinessException ex,
      HttpServletRequest request) {
    log.error("Business exception: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Business Error")
        .message(ex.getMessage())
        .path(request.getRequestURI())
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex,
      HttpServletRequest request) {
    log.error("Validation error: {}", ex.getMessage());

    List<ValidationError> validationErrors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> ErrorResponse.ValidationError.builder()
            .field(error.getField())
            .message(error.getDefaultMessage())
            .build())
        .collect(Collectors.toList());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Validation Error")
        .message("Validation failed for one or more fields")
        .path(request.getRequestURI())
        .validationErrors(validationErrors)
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentialsException(
      BadCredentialsException ex,
      HttpServletRequest request) {
    log.error("Authentication failed: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.UNAUTHORIZED.value())
        .error("Authentication Error")
        .message("Invalid email or password")
        .path(request.getRequestURI())
        .build();

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(
      UsernameNotFoundException ex,
      HttpServletRequest request) {
    log.error("User not found: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.NOT_FOUND.value())
        .error("Not Found")
        .message(ex.getMessage())
        .path(request.getRequestURI())
        .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(
      Exception ex,
      HttpServletRequest request) {
    log.error("Unexpected error occurred", ex);

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error("Internal Server Error")
        .message("An unexpected error occurred. Please try again later.")
        .path(request.getRequestURI())
        .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}
