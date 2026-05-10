package com.eaglebank.controller;

import com.eaglebank.dto.AuthResponse;
import com.eaglebank.dto.SetPasswordRequest;
import com.eaglebank.service.AuthService;
import lombok.RequiredArgsConstructor;
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
  public AuthResponse token(
      @RequestHeader(value = "Authorization", required = false) String authorization) {
    return new AuthResponse(authService.authenticateBasic(authorization));
  }

  @PostMapping("/set-password")
  public void setPassword(@RequestBody SetPasswordRequest req) {
    authService.setPassword(req);
  }
}
