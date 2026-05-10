package com.eaglebank.util;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

public final class AuthUtils {
  private AuthUtils() {}

  public static String requireAuthenticatedUsername() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
    }
    return auth.getName();
  }
}
