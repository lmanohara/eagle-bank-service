package com.eaglebank.exception;

import com.eaglebank.dto.ErrorResponse;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<?> apiException(ApiException exception) {
    ErrorResponse errorResponse = ErrorResponse.builder().message(exception.getMessage()).build();
    return ResponseEntity.status(exception.getStatus()).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> validation(MethodArgumentNotValidException exception) {
    Map<String, String> errors = new HashMap<>();
    exception
        .getBindingResult()
        .getFieldErrors()
        .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
    return ResponseEntity.badRequest().body(errors);
  }
}
