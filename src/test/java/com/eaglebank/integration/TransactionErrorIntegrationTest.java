package com.eaglebank.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eaglebank.dto.AccountResponse;
import com.eaglebank.dto.TransactionRequest;
import com.eaglebank.model.TransactionType;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

class TransactionErrorIntegrationTest extends AbstractIntegrationTest {

  @Test
  void withdrawingMoreThanBalance_returnsUnprocessableEntity() throws Exception {
    UserContext alice = onboardUser("Alice");
    AccountResponse account = createAccount(alice.bearer(), "Everyday", "personal");
    postTransaction(
        alice.bearer(),
        account.getAccountNumber(),
        TransactionType.DEPOSIT,
        new BigDecimal("50.00"),
        "deposit");

    TransactionRequest overdraw =
        TransactionRequest.builder()
            .amount(new BigDecimal("100.00"))
            .currency("GBP")
            .type(TransactionType.WITHDRAW)
            .reference("overdraw")
            .build();

    mvc.perform(
            post("/v1/accounts/{accountNumber}/transactions", account.getAccountNumber())
                .header(HttpHeaders.AUTHORIZATION, alice.bearer())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(overdraw)))
        .andExpect(status().isUnprocessableContent())
        .andExpect(jsonPath("$.message").value("Insufficient funds"));
  }
}
