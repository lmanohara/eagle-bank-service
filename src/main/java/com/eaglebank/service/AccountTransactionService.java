package com.eaglebank.service;

import com.eaglebank.dto.TransactionRequest;
import com.eaglebank.dto.TransactionResponse;
import com.eaglebank.dto.TransactionsResponse;
import com.eaglebank.entity.Account;
import com.eaglebank.entity.AccountTransaction;
import com.eaglebank.exception.BadRequestException;
import com.eaglebank.exception.ResourceNotFoundException;
import com.eaglebank.model.TransactionType;
import com.eaglebank.repository.AccountRepository;
import com.eaglebank.repository.AccountTransactionRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountTransactionService {

  private final AccountRepository accountRepository;
  private final AccountTransactionRepository transactionRepository;

  @PreAuthorize("@authChecker.canAccessAccount(#accountNumber, authentication.name)")
  @Transactional
  public TransactionResponse createTransaction(
      String accountNumber, TransactionRequest transactionRequest) {
    Account account =
        accountRepository
            .findByAccountNumber(accountNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

    Account savedAccount = updateAccountBalance(transactionRequest, account);

    AccountTransaction savedTransaction = saveTransaction(transactionRequest, savedAccount);

    String txId = savedTransaction.getId();
    String userId =
        savedTransaction.getAccount() != null && savedTransaction.getAccount().getUser() != null
            ? savedTransaction.getAccount().getUser().getId()
            : null;

    return TransactionResponse.builder()
        .id(txId)
        .amount(savedTransaction.getAmount())
        .currency(savedTransaction.getCurrency())
        .type(savedTransaction.getType())
        .reference(savedTransaction.getReference())
        .userId(userId)
        .createdTimestamp(savedTransaction.getCreatedTimestamp())
        .build();
  }

  @PreAuthorize("@authChecker.canAccessAccount(#accountNumber, authentication.name)")
  public TransactionsResponse listTransactions(String accountNumber) {
    Account account =
        accountRepository
            .findByAccountNumber(accountNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

    List<AccountTransaction> transactions = transactionRepository.findByAccount(account);

    List<TransactionResponse> responses =
        transactions.stream()
            .map(
                transaction -> {
                  String txId = transaction.getId();
                  String userId =
                      transaction.getAccount() != null && transaction.getAccount().getUser() != null
                          ? transaction.getAccount().getUser().getId()
                          : null;
                  return TransactionResponse.builder()
                      .id(txId)
                      .amount(transaction.getAmount())
                      .currency(transaction.getCurrency())
                      .type(transaction.getType())
                      .reference(transaction.getReference())
                      .userId(userId)
                      .createdTimestamp(transaction.getCreatedTimestamp())
                      .build();
                })
            .collect(Collectors.toList());

    return TransactionsResponse.builder().transactions(responses).build();
  }

  private AccountTransaction saveTransaction(
      TransactionRequest transactionRequest, Account savedAccount) {
    AccountTransaction transaction =
        AccountTransaction.builder()
            .id("trn-" + UUID.randomUUID())
            .account(savedAccount)
            .amount(transactionRequest.getAmount())
            .currency(transactionRequest.getCurrency())
            .type(transactionRequest.getType())
            .reference(transactionRequest.getReference())
            .createdTimestamp(Instant.now())
            .updatedTimestamp(Instant.now())
            .build();

    return transactionRepository.save(transaction);
  }

  private Account updateAccountBalance(TransactionRequest transactionRequest, Account account) {
    if (transactionRequest.getAmount() == null
        || transactionRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new BadRequestException("Invalid amount");
    }

    BigDecimal newBalance;
    TransactionType type = transactionRequest.getType();

    if (type == TransactionType.DEPOSIT) {
      newBalance =
          account.getBalance() == null
              ? transactionRequest.getAmount()
              : account.getBalance().add(transactionRequest.getAmount());
    } else if (type == TransactionType.WITHDRAW) {
      BigDecimal current = account.getBalance() == null ? BigDecimal.ZERO : account.getBalance();
      if (current.compareTo(transactionRequest.getAmount()) < 0) {
        throw new BadRequestException("Insufficient funds");
      }
      newBalance = current.subtract(transactionRequest.getAmount());
    } else {
      throw new BadRequestException("Invalid transaction type");
    }

    account.setBalance(newBalance);
    account.setUpdatedTimestamp(Instant.now());

    return accountRepository.save(account);
  }

  @PreAuthorize("@authChecker.canAccessAccount(#accountNumber, authentication.name)")
  public TransactionResponse getTransaction(String accountNumber, String transactionId) {
    Account account =
        accountRepository
            .findByAccountNumber(accountNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

    AccountTransaction transaction =
        transactionRepository
            .findByIdAndAccount(transactionId, account)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

    String userId =
        transaction.getAccount() != null && transaction.getAccount().getUser() != null
            ? transaction.getAccount().getUser().getId()
            : null;

    return TransactionResponse.builder()
        .id(transaction.getId())
        .amount(transaction.getAmount())
        .currency(transaction.getCurrency())
        .type(transaction.getType())
        .reference(transaction.getReference())
        .userId(userId)
        .createdTimestamp(transaction.getCreatedTimestamp())
        .build();
  }
}
