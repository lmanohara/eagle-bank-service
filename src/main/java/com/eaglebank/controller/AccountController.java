package com.eaglebank.controller;

import com.eaglebank.dto.AccountRequest;
import com.eaglebank.dto.AccountResponse;
import com.eaglebank.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;

  @PostMapping
  public AccountResponse create(@Valid @RequestBody AccountRequest req) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
      throw new org.springframework.web.server.ResponseStatusException(
          org.springframework.http.HttpStatus.UNAUTHORIZED, "Not authenticated");
    }

    String username = auth.getName();
    return accountService.createForUser(username, req);
  }
}
