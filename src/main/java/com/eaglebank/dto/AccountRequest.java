package com.eaglebank.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AccountRequest {

  @NotBlank private String name;

  @NotBlank private String accountType;
}
