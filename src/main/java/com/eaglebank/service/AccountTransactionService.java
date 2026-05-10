package com.eaglebank.service;

import com.eaglebank.dto.TransactionRequest;
import com.eaglebank.dto.TransactionResponse;
import com.eaglebank.dto.TransactionsResponse;
import com.eaglebank.entity.Account;
import com.eaglebank.entity.AccountTransaction;
import com.eaglebank.model.TransactionType;
import com.eaglebank.repository.AccountRepository;
import com.eaglebank.repository.AccountTransactionRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountTransactionService {

  private final AccountRepository accountRepository;
  private final AccountTransactionRepository transactionRepository;

  @Transactional
  public TransactionResponse createTransaction(
      Long accountId, String username, TransactionRequest transactionRequest) {
    Account account =
        accountRepository
            .findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));

    if (account.getUser() == null
        || account.getUser().getUsername() == null
        || !account.getUser().getUsername().equals(username)) {
      throw new RuntimeException("Forbidden");
    }

    Account savedAccount = updateAccountBalance(transactionRequest, account);

    AccountTransaction saved = saveTransaction(transactionRequest, savedAccount);

    // Convert amount to minor units (pence) as a long
    Long amountMinor = null;
    if (saved.getAmount() != null) {
      amountMinor = saved.getAmount().movePointRight(2).longValue();
    }

    String txId = "tan-" + saved.getId();
    String userId =
        saved.getAccount() != null && saved.getAccount().getUser() != null
            ? "usr-" + saved.getAccount().getUser().getId()
            : null;

    return TransactionResponse.builder()
        .id(txId)
        .amount(amountMinor)
        .currency(saved.getCurrency())
        .type(saved.getType())
        .reference(saved.getReference())
        .userId(userId)
        .createdTimestamp(saved.getCreatedTimestamp())
        .build();
  }

  public TransactionsResponse listTransactions(Long accountId, String username) {
    Account account =
        accountRepository
            .findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));

    if (account.getUser() == null
        || account.getUser().getUsername() == null
        || !account.getUser().getUsername().equals(username)) {
      throw new RuntimeException("Forbidden");
    }

    List<AccountTransaction> transactions = transactionRepository.findByAccount(account);

    List<TransactionResponse> responses =
        transactions.stream()
            .map(
                saved -> {
                  Long amountMinor = null;
                  if (saved.getAmount() != null) {
                    amountMinor = saved.getAmount().movePointRight(2).longValue();
                  }
                  String txId = "tan-" + saved.getId();
                  String userId =
                      saved.getAccount() != null && saved.getAccount().getUser() != null
                          ? "usr-" + saved.getAccount().getUser().getId()
                          : null;
                  return TransactionResponse.builder()
                      .id(txId)
                      .amount(amountMinor)
                      .currency(saved.getCurrency())
                      .type(saved.getType())
                      .reference(saved.getReference())
                      .userId(userId)
                      .createdTimestamp(saved.getCreatedTimestamp())
                      .build();
                })
            .collect(Collectors.toList());

    return TransactionsResponse.builder().transactions(responses).build();
  }

  private AccountTransaction saveTransaction(
      TransactionRequest transactionRequest, Account savedAccount) {
    AccountTransaction transaction =
        AccountTransaction.builder()
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

  private Account updateAccountBalance(TransactionRequest req, Account account) {
    if (req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new RuntimeException("Invalid amount");
    }

    BigDecimal newBalance;
    TransactionType type = req.getType();
    if (type == TransactionType.DEPOSIT) {
      newBalance =
          account.getBalance() == null
              ? req.getAmount()
              : account.getBalance().add(req.getAmount());
    } else if (type == TransactionType.WITHDRAW) {
      BigDecimal current = account.getBalance() == null ? BigDecimal.ZERO : account.getBalance();
      if (current.compareTo(req.getAmount()) < 0) {
        throw new RuntimeException("Insufficient funds");
      }
      newBalance = current.subtract(req.getAmount());
    } else {
      throw new RuntimeException("Invalid transaction type");
    }

    account.setBalance(newBalance);
    account.setUpdatedTimestamp(Instant.now());

    return accountRepository.save(account);
  }
}
