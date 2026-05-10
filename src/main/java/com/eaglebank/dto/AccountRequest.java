package com.eaglebank.dto;

import lombok.Data;

@Data
public class AccountRequest {
  private String name;
  private String accountType;
}
