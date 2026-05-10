package com.eaglebank.util;

import net.datafaker.Faker;

public class AccountNumberGenerator {

  private static final Faker FAKER = new Faker();

  public static String generateAccountNumber() {
    return FAKER.numerify("########");
  }

  public static String generateSortCode() {
    return FAKER.numerify("##-##-##");
  }
}
