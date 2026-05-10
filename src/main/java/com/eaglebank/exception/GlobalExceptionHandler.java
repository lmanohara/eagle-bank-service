package com.eaglebank.exception;

import com.eaglebank.dto.ErrorResponse;
import com.eaglebank.dto.ValidationError;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ErrorResponse> apiException(ApiException exception) {
    ErrorResponse errorResponse = ErrorResponse.builder().message(exception.getMessage()).build();

    return ResponseEntity.status(exception.getStatus()).body(errorResponse);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> badCredentials(BadCredentialsException exception) {
    ErrorResponse errorResponse = ErrorResponse.builder().message(exception.getMessage()).build();
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException exception) {
    List<ValidationError> details =
        exception.getBindingResult().getFieldErrors().stream()
            .map(
                e ->
                    ValidationError.builder()
                        .field(e.getField())
                        .message(e.getDefaultMessage())
                        .type(e.getCode())
                        .build())
            .toList();

    ErrorResponse errorResponse =
        ErrorResponse.builder().message("Validation failed").details(details).build();

    return ResponseEntity.badRequest().body(errorResponse);
  }
}
