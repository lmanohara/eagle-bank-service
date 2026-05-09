package com.eaglebank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto {
  private String line1;
  private String line2;
  private String line3;
  private String town;
  private String county;
  private String postcode;
}
