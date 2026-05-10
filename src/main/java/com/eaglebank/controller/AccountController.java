package com.eaglebank.controller;

import com.eaglebank.dto.AccountRequest;
import com.eaglebank.dto.AccountResponse;
import com.eaglebank.dto.AccountsResponse;
import com.eaglebank.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;

  @PostMapping
  public AccountResponse create(
      @Valid @RequestBody AccountRequest req, @AuthenticationPrincipal String authUsername) {
    if (authUsername == null) {
      throw new org.springframework.web.server.ResponseStatusException(
          org.springframework.http.HttpStatus.UNAUTHORIZED, "Not authenticated");
    }
    return accountService.createForUser(authUsername, req);
  }

  @GetMapping
  public AccountsResponse listForUser(@AuthenticationPrincipal String authUsername) {
    if (authUsername == null) {
      throw new org.springframework.web.server.ResponseStatusException(
          org.springframework.http.HttpStatus.UNAUTHORIZED, "Not authenticated");
    }
    return accountService.listForUser(authUsername);
  }

  @GetMapping("/{accountNumber}")
  public AccountResponse getOne(
      @PathVariable("accountNumber") String accountNumber,
      @AuthenticationPrincipal String authUsername) {
    if (authUsername == null) {
      throw new org.springframework.web.server.ResponseStatusException(
          org.springframework.http.HttpStatus.UNAUTHORIZED, "Not authenticated");
    }

    try {
      return accountService.getByAccountNumberForUser(accountNumber, authUsername);
    } catch (RuntimeException e) {
      if ("Forbidden".equals(e.getMessage())) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not allowed");
      }
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
    }
  }
}
