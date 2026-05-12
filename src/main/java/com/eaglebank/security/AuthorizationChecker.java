package com.eaglebank.security;

import com.eaglebank.entity.Account;
import com.eaglebank.entity.User;
import com.eaglebank.repository.AccountRepository;
import com.eaglebank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("authChecker")
@RequiredArgsConstructor
public class AuthorizationChecker {

  private final UserRepository userRepository;
  private final AccountRepository accountRepository;

  public boolean canAccessUser(String userId, String username) {
    if (userId == null || username == null) {
      return false;
    }
    return userRepository
        .findById(userId)
        .map(User::getUsername)
        .map(username::equals)
        .orElse(true);
  }

  public boolean canAccessAccount(String accountNumber, String username) {
    if (accountNumber == null || username == null) {
      return false;
    }
    return accountRepository
        .findByAccountNumber(accountNumber)
        .map(Account::getUser)
        .map(User::getUsername)
        .map(username::equals)
        .orElse(true);
  }
}
