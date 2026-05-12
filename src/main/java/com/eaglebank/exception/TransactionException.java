package com.eaglebank.exception;

import org.springframework.http.HttpStatus;

public class TransactionException extends ApiException {

  private TransactionException(HttpStatus status, String message) {
    super(status, message);
  }

  public static TransactionException insufficientFunds() {
    return new TransactionException(HttpStatus.UNPROCESSABLE_CONTENT, "Insufficient funds");
  }

  public static TransactionException invalidAmount() {
    return new TransactionException(HttpStatus.BAD_REQUEST, "Invalid amount");
  }

  public static TransactionException invalidTransactionType() {
    return new TransactionException(HttpStatus.BAD_REQUEST, "Invalid transaction type");
  }
}
