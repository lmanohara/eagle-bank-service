package com.eaglebank.exception;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends ApiException {

  public AccessDeniedException(String message) {
    super(HttpStatus.FORBIDDEN, message);
  }

  public AccessDeniedException() {
    super(HttpStatus.FORBIDDEN, "Forbidden");
  }
}
