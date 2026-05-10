package com.eaglebank.service;

import com.eaglebank.dto.AccountRequest;
import com.eaglebank.dto.AccountResponse;
import com.eaglebank.entity.Account;
import com.eaglebank.entity.User;
import com.eaglebank.repository.AccountRepository;
import com.eaglebank.repository.UserRepository;
import com.eaglebank.util.AccountNumberGenerator;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

  private final AccountRepository accountRepository;
  private final UserRepository userRepository;

  public AccountResponse createForUser(String username, AccountRequest req) {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Instant now = Instant.now();
    Account account =
        Account.builder()
            .accountNumber(AccountNumberGenerator.generateAccountNumber())
            .sortCode(AccountNumberGenerator.generateSortCode())
            .name(req.getName())
            .accountType(req.getAccountType())
            .balance(0L)
            .currency("GBP")
            .createdTimestamp(now)
            .updatedTimestamp(now)
            .user(user)
            .build();

    Account saved = accountRepository.save(account);

    return AccountResponse.builder()
        .accountNumber(saved.getAccountNumber())
        .sortCode(saved.getSortCode())
        .name(saved.getName())
        .accountType(saved.getAccountType())
        .balance(saved.getBalance())
        .currency(saved.getCurrency())
        .createdTimestamp(saved.getCreatedTimestamp())
        .updatedTimestamp(saved.getUpdatedTimestamp())
        .build();
  }
}
