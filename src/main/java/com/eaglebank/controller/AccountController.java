package com.eaglebank.controller;

import com.eaglebank.dto.AccountRequest;
import com.eaglebank.dto.AccountResponse;
import com.eaglebank.dto.AccountsResponse;
import com.eaglebank.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  public ResponseEntity<AccountResponse> create(
      @Valid @RequestBody AccountRequest req, @AuthenticationPrincipal String authUsername) {
    return ResponseEntity.ok(accountService.createForUser(authUsername, req));
  }

  @GetMapping
  public ResponseEntity<AccountsResponse> listForUser(
      @AuthenticationPrincipal String authUsername) {
    return ResponseEntity.ok(accountService.listForUser(authUsername));
  }

  @GetMapping("/{accountNumber}")
  public ResponseEntity<AccountResponse> getOne(
      @PathVariable("accountNumber") String accountNumber) {
    return ResponseEntity.ok(accountService.getByAccountNumberForUser(accountNumber));
  }
}
