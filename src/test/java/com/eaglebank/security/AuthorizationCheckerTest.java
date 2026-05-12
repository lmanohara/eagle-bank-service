package com.eaglebank.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.eaglebank.entity.Account;
import com.eaglebank.entity.User;
import com.eaglebank.repository.AccountRepository;
import com.eaglebank.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthorizationCheckerTest {

  @InjectMocks private AuthorizationChecker checker;

  @Mock private UserRepository userRepository;
  @Mock private AccountRepository accountRepository;

  @Test
  void canAccessUser_returnsFalse_whenUserIdIsNull() {
    assertThat(checker.canAccessUser(null, "alice")).isFalse();
    verifyNoInteractions(userRepository);
  }

  @Test
  void canAccessUser_returnsFalse_whenUsernameIsNull() {
    assertThat(checker.canAccessUser("usr-1", null)).isFalse();
    verifyNoInteractions(userRepository);
  }

  @Test
  void canAccessUser_returnsTrue_whenUserNotFound() {
    when(userRepository.findById("usr-missing")).thenReturn(Optional.empty());

    assertThat(checker.canAccessUser("usr-missing", "alice")).isTrue();
  }

  @Test
  void canAccessUser_returnsTrue_whenUsernameMatches() {
    User user = User.builder().id("usr-1").username("alice").build();
    when(userRepository.findById("usr-1")).thenReturn(Optional.of(user));

    assertThat(checker.canAccessUser("usr-1", "alice")).isTrue();
  }

  @Test
  void canAccessUser_returnsFalse_whenUsernameDoesNotMatch() {
    User user = User.builder().id("usr-1").username("alice").build();
    when(userRepository.findById("usr-1")).thenReturn(Optional.of(user));

    assertThat(checker.canAccessUser("usr-1", "bob")).isFalse();
  }

  @Test
  void canAccessAccount_returnsFalse_whenAccountNumberIsNull() {
    assertThat(checker.canAccessAccount(null, "alice")).isFalse();
    verifyNoInteractions(accountRepository);
  }

  @Test
  void canAccessAccount_returnsFalse_whenUsernameIsNull() {
    assertThat(checker.canAccessAccount("00000001", null)).isFalse();
    verifyNoInteractions(accountRepository);
  }

  @Test
  void canAccessAccount_returnsTrue_whenAccountNotFound() {
    when(accountRepository.findByAccountNumber("00000001")).thenReturn(Optional.empty());

    assertThat(checker.canAccessAccount("00000001", "alice")).isTrue();
  }

  @Test
  void canAccessAccount_returnsTrue_whenOwnerUsernameMatches() {
    User owner = User.builder().id("usr-1").username("alice").build();
    Account account = Account.builder().id("acc-1").accountNumber("00000001").user(owner).build();
    when(accountRepository.findByAccountNumber("00000001")).thenReturn(Optional.of(account));

    assertThat(checker.canAccessAccount("00000001", "alice")).isTrue();
  }

  @Test
  void canAccessAccount_returnsFalse_whenOwnerUsernameDoesNotMatch() {
    User owner = User.builder().id("usr-1").username("alice").build();
    Account account = Account.builder().id("acc-1").accountNumber("00000001").user(owner).build();
    when(accountRepository.findByAccountNumber("00000001")).thenReturn(Optional.of(account));

    assertThat(checker.canAccessAccount("00000001", "bob")).isFalse();
  }
}
