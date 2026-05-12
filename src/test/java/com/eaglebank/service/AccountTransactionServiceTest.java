package com.eaglebank.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import com.eaglebank.dto.TransactionRequest;
import com.eaglebank.dto.TransactionResponse;
import com.eaglebank.entity.Account;
import com.eaglebank.entity.AccountTransaction;
import com.eaglebank.entity.User;
import com.eaglebank.model.TransactionType;
import com.eaglebank.repository.AccountRepository;
import com.eaglebank.repository.AccountTransactionRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountTransactionServiceTest {

  @InjectMocks private AccountTransactionService service;
  @Mock private AccountRepository accountRepository;
  @Mock private AccountTransactionRepository transactionRepository;

  @Test
  void create_deposit_success() {
    User user = User.builder().id("usr-1").username("alice").build();
    Account acc =
        Account.builder()
            .id("acc-1")
            .accountNumber("00000001")
            .balance(BigDecimal.ZERO)
            .user(user)
            .build();

    Mockito.when(accountRepository.findByAccountNumber("00000001")).thenReturn(Optional.of(acc));
    Mockito.when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

    AccountTransaction savedTx =
        AccountTransaction.builder()
            .id("trn-" + UUID.randomUUID().toString())
            .account(acc)
            .amount(BigDecimal.valueOf(10.99))
            .currency("GBP")
            .type(TransactionType.DEPOSIT)
            .reference("ref")
            .createdTimestamp(Instant.now())
            .build();

    Mockito.when(transactionRepository.save(any(AccountTransaction.class))).thenReturn(savedTx);

    TransactionRequest req =
        TransactionRequest.builder()
            .amount(BigDecimal.valueOf(10.99))
            .currency("GBP")
            .type(TransactionType.DEPOSIT)
            .reference("ref")
            .build();

    TransactionResponse resp = service.createTransaction("00000001", req);

    assertThat(resp).isNotNull();
    assertThat(resp.getId()).isEqualTo(savedTx.getId());
    assertThat(resp.getAmount()).isEqualTo(BigDecimal.valueOf(10.99));
    assertThat(resp.getCurrency()).isEqualTo("GBP");
    assertThat(resp.getType()).isEqualTo(TransactionType.DEPOSIT);
  }

  @Test
  void create_withdraw_insufficientFunds() {
    User user = User.builder().id("usr-1").username("alice").build();
    Account acc =
        Account.builder()
            .id("acc-1")
            .accountNumber("00000002")
            .balance(BigDecimal.valueOf(5))
            .user(user)
            .build();

    Mockito.when(accountRepository.findByAccountNumber("00000002")).thenReturn(Optional.of(acc));

    TransactionRequest req =
        TransactionRequest.builder()
            .amount(BigDecimal.valueOf(10))
            .currency("GBP")
            .type(TransactionType.WITHDRAW)
            .reference("r")
            .build();

    assertThatThrownBy(() -> service.createTransaction("00000002", req))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Insufficient funds");
  }

  @Test
  void getTransaction_success() {
    User user = User.builder().id("usr-1").username("alice").build();
    Account acc = Account.builder().id("acc-1").accountNumber("00000003").user(user).build();
    AccountTransaction tx =
        AccountTransaction.builder()
            .id("trn-1")
            .account(acc)
            .amount(BigDecimal.valueOf(2.50))
            .currency("GBP")
            .type(TransactionType.DEPOSIT)
            .reference("r")
            .createdTimestamp(Instant.now())
            .build();

    Mockito.when(accountRepository.findByAccountNumber("00000003")).thenReturn(Optional.of(acc));
    Mockito.when(transactionRepository.findByIdAndAccount("trn-1", acc))
        .thenReturn(Optional.of(tx));

    TransactionResponse resp = service.getTransaction("00000003", "trn-1");

    assertThat(resp).isNotNull();
    assertThat(resp.getId()).isEqualTo("trn-1");
    assertThat(resp.getAmount()).isEqualTo(BigDecimal.valueOf(2.5));
  }
}
