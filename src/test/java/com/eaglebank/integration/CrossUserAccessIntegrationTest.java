package com.eaglebank.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eaglebank.dto.AccountResponse;
import com.eaglebank.dto.TransactionRequest;
import com.eaglebank.dto.TransactionResponse;
import com.eaglebank.model.TransactionType;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

class CrossUserAccessIntegrationTest extends AbstractIntegrationTest {

  @Test
  void userCannotAccessAnotherUsersResources() throws Exception {
    SeededUser alice = onboardUserWithSeedAccount("Alice");
    SeededUser bob = onboardUserWithSeedAccount("Bob");

    mvc.perform(
            get("/v1/users/{userId}", bob.userId())
                .header(HttpHeaders.AUTHORIZATION, alice.bearer()))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("Forbidden"));

    mvc.perform(
            get("/v1/accounts/{accountNumber}", bob.accountNumber())
                .header(HttpHeaders.AUTHORIZATION, alice.bearer()))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("Forbidden"));

    mvc.perform(
            get("/v1/accounts/{accountNumber}/transactions", bob.accountNumber())
                .header(HttpHeaders.AUTHORIZATION, alice.bearer()))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("Forbidden"));

    mvc.perform(
            get(
                    "/v1/accounts/{accountNumber}/transactions/{transactionId}",
                    bob.accountNumber(),
                    bob.transactionId())
                .header(HttpHeaders.AUTHORIZATION, alice.bearer()))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("Forbidden"));

    TransactionRequest intruderDeposit =
        TransactionRequest.builder()
            .amount(new BigDecimal("1.00"))
            .currency("GBP")
            .type(TransactionType.DEPOSIT)
            .reference("intruder")
            .build();

    mvc.perform(
            post("/v1/accounts/{accountNumber}/transactions", bob.accountNumber())
                .header(HttpHeaders.AUTHORIZATION, alice.bearer())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(intruderDeposit)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("Forbidden"));

    mvc.perform(
            get("/v1/accounts/{accountNumber}", alice.accountNumber())
                .header(HttpHeaders.AUTHORIZATION, alice.bearer()))
        .andExpect(status().isOk());

    mvc.perform(
            get("/v1/accounts/{accountNumber}", bob.accountNumber())
                .header(HttpHeaders.AUTHORIZATION, bob.bearer()))
        .andExpect(status().isOk());
  }

  private SeededUser onboardUserWithSeedAccount(String displayName) throws Exception {
    UserContext ctx = onboardUser(displayName);
    AccountResponse account = createAccount(ctx.bearer(), displayName + " Account", "personal");
    TransactionResponse tx =
        postTransaction(
            ctx.bearer(),
            account.getAccountNumber(),
            TransactionType.DEPOSIT,
            new BigDecimal("50.00"),
            "seed-" + displayName);
    return new SeededUser(ctx.userId(), ctx.bearer(), account.getAccountNumber(), tx.getId());
  }

  private record SeededUser(
      String userId, String bearer, String accountNumber, String transactionId) {}
}
