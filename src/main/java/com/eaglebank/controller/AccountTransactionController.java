package com.eaglebank.controller;

import com.eaglebank.dto.TransactionRequest;
import com.eaglebank.dto.TransactionResponse;
import com.eaglebank.dto.TransactionsResponse;
import com.eaglebank.service.AccountTransactionService;
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
public class AccountTransactionController {

  private final AccountTransactionService accountTransactionService;

  @PostMapping("/{accountId}/transactions")
  public TransactionResponse transact(
      @PathVariable("accountId") String accountId, @Valid @RequestBody TransactionRequest req) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
    }

    Long id;
    try {
      id = Long.valueOf(accountId);
    } catch (NumberFormatException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid account id");
    }

    try {
      return accountTransactionService.createTransaction(id, auth.getName(), req);
    } catch (RuntimeException e) {
      String msg = e.getMessage();
      if ("Forbidden".equals(msg)) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not allowed");
      } else if ("Insufficient funds".equals(msg)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds");
      } else if ("Invalid amount".equals(msg) || "Invalid transaction type".equals(msg)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
      }
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
    }
  }

  @GetMapping("/{accountId}/transactions")
  public TransactionsResponse listTransactions(@PathVariable("accountId") String accountId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
    }

    Long id;
    try {
      id = Long.valueOf(accountId);
    } catch (NumberFormatException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid account id");
    }

    try {
      return accountTransactionService.listTransactions(id, auth.getName());
    } catch (RuntimeException e) {
      String msg = e.getMessage();
      if ("Forbidden".equals(msg)) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not allowed");
      }
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
    }
  }

  @GetMapping("/{accountId}/transactions/{transactionId}")
  public TransactionResponse getTransaction(
      @PathVariable("accountId") String accountId,
      @PathVariable("transactionId") String transactionId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
    }

    Long accId;
    try {
      accId = Long.valueOf(accountId);
    } catch (NumberFormatException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid account id");
    }

    Long txId;
    try {
      txId = Long.valueOf(transactionId);
    } catch (NumberFormatException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid transaction id");
    }

    try {
      return accountTransactionService.getTransaction(accId, txId, auth.getName());
    } catch (RuntimeException e) {
      String msg = e.getMessage();
      if ("Forbidden".equals(msg)) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not allowed");
      }
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found");
    }
  }
}
