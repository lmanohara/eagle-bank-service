package com.eaglebank.controller;

import com.eaglebank.dto.AuthResponse;
import com.eaglebank.dto.MessageResponse;
import com.eaglebank.dto.SetPasswordRequest;
import com.eaglebank.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/token")
  public ResponseEntity<AuthResponse> token(
      @RequestHeader(value = "Authorization", required = false) String authorization) {
    return ResponseEntity.ok(
        AuthResponse.builder().token(authService.authenticateBasic(authorization)).build());
  }

  @PostMapping("/set-password")
  public ResponseEntity<MessageResponse> setPassword(@RequestBody SetPasswordRequest request) {
    return ResponseEntity.ok(authService.setPassword(request));
  }
}
