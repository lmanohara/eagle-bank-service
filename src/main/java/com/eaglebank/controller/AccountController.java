package com.eaglebank.controller;

import com.eaglebank.dto.AccountRequest;
import com.eaglebank.dto.AccountResponse;
import com.eaglebank.dto.AccountsResponse;
import com.eaglebank.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
  public AccountResponse create(@Valid @RequestBody AccountRequest req) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
    }

    String username = auth.getName();
    return accountService.createForUser(username, req);
  }

  @GetMapping
  public AccountsResponse listForUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
    }

    String username = auth.getName();
    return accountService.listForUser(username);
  }

  @GetMapping("/{accountNumber}")
  public AccountResponse getOne(@PathVariable("accountNumber") String accountNumber) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
    }

    try {
      return accountService.getByAccountNumberForUser(accountNumber, auth.getName());
    } catch (RuntimeException e) {
      if ("Forbidden".equals(e.getMessage())) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not allowed");
      }
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
    }
  }
}
