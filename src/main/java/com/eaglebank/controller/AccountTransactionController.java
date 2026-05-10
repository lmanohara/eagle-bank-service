package com.eaglebank.controller;

import com.eaglebank.dto.TransactionRequest;
import com.eaglebank.dto.TransactionResponse;
import com.eaglebank.dto.TransactionsResponse;
import com.eaglebank.service.AccountTransactionService;
import com.eaglebank.util.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

  @PostMapping("/{accountNumber}/transactions")
  public TransactionResponse transact(
      @PathVariable("accountNumber") String accountNumber,
      @Valid @RequestBody TransactionRequest req) {
    String username = AuthUtils.requireAuthenticatedUsername();

    try {
      return accountTransactionService.createTransaction(accountNumber, username, req);
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

  @GetMapping("/{accountNumber}/transactions")
  public TransactionsResponse listTransactions(
      @PathVariable("accountNumber") String accountNumber) {
    String username = AuthUtils.requireAuthenticatedUsername();

    try {
      return accountTransactionService.listTransactions(accountNumber, username);
    } catch (RuntimeException e) {
      String msg = e.getMessage();
      if ("Forbidden".equals(msg)) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not allowed");
      }
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
    }
  }

  @GetMapping("/{accountNumber}/transactions/{transactionId}")
  public TransactionResponse getTransaction(
      @PathVariable("accountNumber") String accountNumber,
      @PathVariable("transactionId") String transactionId) {
    String username = AuthUtils.requireAuthenticatedUsername();

    try {
      return accountTransactionService.getTransaction(accountNumber, transactionId, username);
    } catch (RuntimeException e) {
      String msg = e.getMessage();
      if ("Forbidden".equals(msg)) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not allowed");
      }
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found");
    }
  }
}
