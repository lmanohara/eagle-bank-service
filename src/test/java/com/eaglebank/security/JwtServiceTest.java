package com.eaglebank.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

public class JwtServiceTest {

  @Test
  void generate_validate_and_extract_username() throws Exception {
    JwtService jwtService = new JwtService();

    // secret must be sufficiently long for HS256 (32 bytes)
    Field secretField = JwtService.class.getDeclaredField("secret");
    secretField.setAccessible(true);
    secretField.set(jwtService, "01234567890123456789012345678901");

    Field expField = JwtService.class.getDeclaredField("expiration");
    expField.setAccessible(true);
    expField.setLong(jwtService, 100000L);

    String token = jwtService.generateToken("alice");
    String extracted = jwtService.extractUsername(token);

    assertThat(extracted).isEqualTo("alice");
    assertThat(jwtService.validate(token)).isTrue();

    // tampered token should be invalid
    String tampered = token + "x";
    assertThat(jwtService.validate(tampered)).isFalse();
  }
}
