package com.eaglebank.controller;

import com.eaglebank.dto.TransactionRequest;
import com.eaglebank.dto.TransactionResponse;
import com.eaglebank.dto.TransactionsResponse;
import com.eaglebank.service.AccountTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/accounts")
@RequiredArgsConstructor
public class AccountTransactionController {

  private final AccountTransactionService accountTransactionService;

  @PostMapping("/{accountNumber}/transactions")
  public ResponseEntity<TransactionResponse> transact(
      @PathVariable("accountNumber") String accountNumber,
      @Valid @RequestBody TransactionRequest req) {
    return ResponseEntity.ok(accountTransactionService.createTransaction(accountNumber, req));
  }

  @GetMapping("/{accountNumber}/transactions")
  public ResponseEntity<TransactionsResponse> listTransactions(
      @PathVariable("accountNumber") String accountNumber) {
    return ResponseEntity.ok(accountTransactionService.listTransactions(accountNumber));
  }

  @GetMapping("/{accountNumber}/transactions/{transactionId}")
  public ResponseEntity<TransactionResponse> getTransaction(
      @PathVariable("accountNumber") String accountNumber,
      @PathVariable("transactionId") String transactionId) {
    return ResponseEntity.ok(
        accountTransactionService.getTransaction(accountNumber, transactionId));
  }
}
