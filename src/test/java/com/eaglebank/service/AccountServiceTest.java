package com.eaglebank.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import com.eaglebank.dto.AccountRequest;
import com.eaglebank.dto.AccountResponse;
import com.eaglebank.entity.Account;
import com.eaglebank.entity.User;
import com.eaglebank.repository.AccountRepository;
import com.eaglebank.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

  @InjectMocks private AccountService accountService;
  @Mock private AccountRepository accountRepository;
  @Mock private UserRepository userRepository;

  @Test
  void createForUser_success() {
    User user = User.builder().id("usr-1").username("alice").build();
    Mockito.when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

    Mockito.when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

    AccountRequest req = new AccountRequest();
    req.setName("Personal");
    req.setAccountType("personal");

    AccountResponse resp = accountService.createForUser("alice", req);

    assertThat(resp).isNotNull();
    assertThat(resp.getAccountNumber()).isNotNull();
    assertThat(resp.getName()).isEqualTo("Personal");
    assertThat(resp.getAccountType()).isEqualTo("personal");
  }

  @Test
  void getByAccountNumberForUser_forbidden() {
    User owner = User.builder().id("usr-1").username("owner").build();
    Account acc = Account.builder().id("acc-1").accountNumber("00000001").user(owner).build();

    Mockito.when(accountRepository.findByAccountNumber("00000001")).thenReturn(Optional.of(acc));

    assertThatThrownBy(() -> accountService.getByAccountNumberForUser("00000001", "alice"))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Forbidden");
  }
}
