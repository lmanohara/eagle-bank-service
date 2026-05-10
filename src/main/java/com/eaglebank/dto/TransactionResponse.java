package com.eaglebank.dto;

import com.eaglebank.model.TransactionType;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
  private String id; // e.g., "tan-123abc"
  private Long amount; // amount in minor units (pence)
  private String currency;
  private TransactionType type;
  private String reference;
  private String userId; // e.g., "usr-abc123"
  private Instant createdTimestamp;
}
