package com.eaglebank.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
  private String line1;
  private String line2;
  private String line3;
  private String town;
  private String county;
  private String postcode;
}
