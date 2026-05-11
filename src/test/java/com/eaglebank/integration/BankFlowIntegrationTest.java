package com.eaglebank.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eaglebank.dto.AccountResponse;
import com.eaglebank.dto.TransactionResponse;
import com.eaglebank.dto.TransactionsResponse;
import com.eaglebank.model.TransactionType;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

class BankFlowIntegrationTest extends AbstractIntegrationTest {

  @Test
  void fullUserAccountTransactionFlow() throws Exception {
    UserContext alice = onboardUser("Alice");

    AccountResponse createdAccount = createAccount(alice.bearer(), "Everyday", "personal");
    assertThat(createdAccount.getAccountNumber()).isNotBlank();
    assertThat(createdAccount.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);

    String accountNumber = createdAccount.getAccountNumber();

    postTransaction(
        alice.bearer(),
        accountNumber,
        TransactionType.DEPOSIT,
        new BigDecimal("100.50"),
        "deposit-1");
    postTransaction(
        alice.bearer(),
        accountNumber,
        TransactionType.DEPOSIT,
        new BigDecimal("25.00"),
        "deposit-2");
    postTransaction(
        alice.bearer(),
        accountNumber,
        TransactionType.WITHDRAW,
        new BigDecimal("30.00"),
        "withdraw-1");

    MvcResult listResult =
        mvc.perform(
                get("/v1/accounts/{accountNumber}/transactions", accountNumber)
                    .header(HttpHeaders.AUTHORIZATION, alice.bearer()))
            .andExpect(status().isOk())
            .andReturn();

    TransactionsResponse transactions =
        objectMapper.readValue(
            listResult.getResponse().getContentAsString(), TransactionsResponse.class);
    assertThat(transactions.getTransactions()).hasSize(3);
    assertThat(transactions.getTransactions())
        .extracting(TransactionResponse::getType)
        .containsExactlyInAnyOrder(
            TransactionType.DEPOSIT, TransactionType.DEPOSIT, TransactionType.WITHDRAW);

    MvcResult getAccountResult =
        mvc.perform(
                get("/v1/accounts/{accountNumber}", accountNumber)
                    .header(HttpHeaders.AUTHORIZATION, alice.bearer()))
            .andExpect(status().isOk())
            .andReturn();

    AccountResponse finalAccount =
        objectMapper.readValue(
            getAccountResult.getResponse().getContentAsString(), AccountResponse.class);
    assertThat(finalAccount.getBalance()).isEqualByComparingTo(new BigDecimal("95.50"));
  }
}
