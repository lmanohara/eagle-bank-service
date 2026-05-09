package com.eaglebank.dto;

import lombok.Data;

@Data
public class SetPasswordRequest {
  private String token;
  private String username;
  private String password;
}
