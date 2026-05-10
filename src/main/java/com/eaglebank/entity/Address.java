package com.eaglebank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
  @Column(name = "line1")
  private String line1;

  @Column(name = "line2")
  private String line2;

  @Column(name = "line3")
  private String line3;

  @Column(name = "town")
  private String town;

  @Column(name = "county")
  private String county;

  @Column(name = "postcode")
  private String postcode;
}
