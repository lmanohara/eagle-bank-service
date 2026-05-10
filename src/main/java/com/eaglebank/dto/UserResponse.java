package com.eaglebank.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
  private String id;
  private String name;
  private AddressDto address;
  private String phoneNumber;
  private String email;
  private Instant createdTimestamp;
  private Instant updatedTimestamp;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String passwordSetupToken;
}
