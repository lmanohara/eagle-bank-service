package com.eaglebank.controller;

import com.eaglebank.dto.AuthResponse;
import com.eaglebank.service.AuthService;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public AuthResponse login(
      @RequestHeader(value = "Authorization", required = false) String authorization) {
    if (authorization == null || !authorization.startsWith("Basic ")) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Basic auth header");
    }

    String base64Creds = authorization.substring(6).trim();
    String decoded;
    try {
      decoded = new String(Base64.getDecoder().decode(base64Creds), StandardCharsets.UTF_8);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Basic auth encoding");
    }

    int idx = decoded.indexOf(':');
    if (idx <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Basic auth format");
    }

    String username = decoded.substring(0, idx);
    String password = decoded.substring(idx + 1);

    try {
      String token = authService.authenticate(username, password);
      return new AuthResponse(token);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
  }
}
