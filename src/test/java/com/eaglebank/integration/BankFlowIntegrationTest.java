package com.eaglebank.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eaglebank.dto.*;
import com.eaglebank.model.TransactionType;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BankFlowIntegrationTest {

  @Autowired private MockMvc mvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void fullUserAccountTransactionFlow() throws Exception {
    String email = "alice.flow+" + System.nanoTime() + "@example.com";
    String username = "alice_flow_" + System.nanoTime();
    String password = "Sup3rSecret!";

    UserRequest userRequest = new UserRequest();
    userRequest.setName("Alice Flow");
    userRequest.setEmail(email);
    userRequest.setPhoneNumber("+447700900000");
    userRequest.setAddress(
        AddressDto.builder()
            .line1("1 Test Street")
            .town("London")
            .county("London")
            .postcode("EC1A 1AA")
            .build());

    MvcResult createUserResult =
        mvc.perform(
                post("/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userRequest)))
            .andExpect(status().isOk())
            .andReturn();

    UserResponse createdUser =
        objectMapper.readValue(
            createUserResult.getResponse().getContentAsString(), UserResponse.class);

    assertThat(createdUser.getId()).startsWith("usr-");
    assertThat(createdUser.getEmail()).isEqualTo(email);
    assertThat(createdUser.getPasswordSetupToken()).isNotBlank();

    SetPasswordRequest setPasswordRequest = new SetPasswordRequest();
    setPasswordRequest.setToken(createdUser.getPasswordSetupToken());
    setPasswordRequest.setUsername(username);
    setPasswordRequest.setPassword(password);

    mvc.perform(
            post("/v1/auth/set-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(setPasswordRequest)))
        .andExpect(status().isOk());

    String basicCreds =
        Base64.getEncoder()
            .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));

    MvcResult tokenResult =
        mvc.perform(post("/v1/auth/token").header(HttpHeaders.AUTHORIZATION, "Basic " + basicCreds))
            .andExpect(status().isOk())
            .andReturn();

    AuthResponse authResponse =
        objectMapper.readValue(tokenResult.getResponse().getContentAsString(), AuthResponse.class);
    String bearer = "Bearer " + authResponse.getToken();
    assertThat(authResponse.getToken()).isNotBlank();

    AccountRequest accountRequest = new AccountRequest();
    accountRequest.setName("Everyday");
    accountRequest.setAccountType("personal");

    MvcResult createAccountResult =
        mvc.perform(
                post("/v1/accounts")
                    .header(HttpHeaders.AUTHORIZATION, bearer)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(accountRequest)))
            .andExpect(status().isOk())
            .andReturn();

    AccountResponse createdAccount =
        objectMapper.readValue(
            createAccountResult.getResponse().getContentAsString(), AccountResponse.class);
    assertThat(createdAccount.getAccountNumber()).isNotBlank();
    assertThat(createdAccount.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);

    String accountNumber = createdAccount.getAccountNumber();

    postTransaction(mvc, bearer, accountNumber, TransactionType.DEPOSIT, new BigDecimal("100.50"));
    postTransaction(mvc, bearer, accountNumber, TransactionType.DEPOSIT, new BigDecimal("25.00"));
    postTransaction(mvc, bearer, accountNumber, TransactionType.WITHDRAW, new BigDecimal("30.00"));

    MvcResult listResult =
        mvc.perform(
                get("/v1/accounts/{accountNumber}/transactions", accountNumber)
                    .header(HttpHeaders.AUTHORIZATION, bearer))
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
                    .header(HttpHeaders.AUTHORIZATION, bearer))
            .andExpect(status().isOk())
            .andReturn();

    AccountResponse finalAccount =
        objectMapper.readValue(
            getAccountResult.getResponse().getContentAsString(), AccountResponse.class);
    assertThat(finalAccount.getBalance()).isEqualByComparingTo(new BigDecimal("95.50"));
  }

  private void postTransaction(
      MockMvc mvc, String bearer, String accountNumber, TransactionType type, BigDecimal amount)
      throws Exception {
    TransactionRequest req =
        TransactionRequest.builder()
            .amount(amount)
            .currency("GBP")
            .type(type)
            .reference("ref-" + type)
            .build();

    mvc.perform(
            post("/v1/accounts/{accountNumber}/transactions", accountNumber)
                .header(HttpHeaders.AUTHORIZATION, bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isOk());
  }
}
