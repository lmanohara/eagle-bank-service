package com.eaglebank.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionType {
  DEPOSIT,
  WITHDRAW;

  @JsonValue
  public String toValue() {
    return name().toLowerCase();
  }

  @JsonCreator
  public static TransactionType forValue(String value) {
    if (value == null) return null;
    return TransactionType.valueOf(value.trim().toUpperCase());
  }
}
