package com.timetracking.tracking.exception;

public class BusinessException extends RuntimeException {
  public BusinessException(String message) {
    super(message);
  }
}