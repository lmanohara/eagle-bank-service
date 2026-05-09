package com.eaglebank.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequest {
  @NotBlank private String name;

  @Valid private AddressDto address;

  private String phoneNumber;

  @Email @NotBlank private String email;
}
