package com.eaglebank.dto;

import com.eaglebank.model.TransactionType;
import java.math.BigDecimal;
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
public class TransactionRequest {
  private BigDecimal amount;
  private String currency;
  private TransactionType type; // DEPOSIT or WITHDRAW
  private String reference;
}
