package com.eaglebank.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eaglebank.dto.AccountRequest;
import com.eaglebank.dto.AccountResponse;
import com.eaglebank.dto.AddressDto;
import com.eaglebank.dto.AuthResponse;
import com.eaglebank.dto.SetPasswordRequest;
import com.eaglebank.dto.TransactionRequest;
import com.eaglebank.dto.TransactionResponse;
import com.eaglebank.dto.UserRequest;
import com.eaglebank.dto.UserResponse;
import com.eaglebank.model.TransactionType;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
abstract class AbstractIntegrationTest {

  @Autowired protected MockMvc mvc;
  @Autowired protected ObjectMapper objectMapper;

  protected UserContext onboardUser(String displayName) throws Exception {
    long nonce = System.nanoTime();
    String email = displayName.toLowerCase() + "+" + nonce + "@example.com";
    String username = displayName.toLowerCase() + "_" + nonce;
    String password = displayName + "Secret1!";

    UserRequest userRequest = new UserRequest();
    userRequest.setName(displayName + " Tester");
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

    return new UserContext(createdUser.getId(), "Bearer " + authResponse.getToken());
  }

  protected AccountResponse createAccount(String bearer, String name, String accountType)
      throws Exception {
    AccountRequest accountRequest = new AccountRequest();
    accountRequest.setName(name);
    accountRequest.setAccountType(accountType);

    MvcResult result =
        mvc.perform(
                post("/v1/accounts")
                    .header(HttpHeaders.AUTHORIZATION, bearer)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(accountRequest)))
            .andExpect(status().isOk())
            .andReturn();

    return objectMapper.readValue(result.getResponse().getContentAsString(), AccountResponse.class);
  }

  protected TransactionResponse postTransaction(
      String bearer,
      String accountNumber,
      TransactionType type,
      BigDecimal amount,
      String reference)
      throws Exception {
    TransactionRequest req =
        TransactionRequest.builder()
            .amount(amount)
            .currency("GBP")
            .type(type)
            .reference(reference)
            .build();

    MvcResult result =
        mvc.perform(
                post("/v1/accounts/{accountNumber}/transactions", accountNumber)
                    .header(HttpHeaders.AUTHORIZATION, bearer)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andReturn();

    return objectMapper.readValue(
        result.getResponse().getContentAsString(), TransactionResponse.class);
  }

  protected record UserContext(String userId, String bearer) {}
}
