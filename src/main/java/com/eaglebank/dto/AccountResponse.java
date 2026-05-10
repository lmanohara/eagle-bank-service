package com.eaglebank.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {
  private String accountNumber;
  private String sortCode;
  private String name;
  private String accountType;
  private Long balance;
  private String currency;
  private Instant createdTimestamp;
  private Instant updatedTimestamp;
}
