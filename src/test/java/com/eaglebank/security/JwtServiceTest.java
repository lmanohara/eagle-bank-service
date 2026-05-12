package com.eaglebank.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import org.junit.jupiter.api.Test;

public class JwtServiceTest {

  @Test
  void generate_validate_and_extract_username() throws Exception {
    JwtService jwtService = new JwtService();

    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
    generator.initialize(2048);
    KeyPair pair = generator.generateKeyPair();

    setField(jwtService, "privateKey", pair.getPrivate());
    setField(jwtService, "publicKey", pair.getPublic());

    Field expField = JwtService.class.getDeclaredField("expiration");
    expField.setAccessible(true);
    expField.setLong(jwtService, 100000L);

    String token = jwtService.generateToken("alice");
    String extracted = jwtService.extractUsername(token);

    assertThat(extracted).isEqualTo("alice");
    assertThat(jwtService.validate(token)).isTrue();

    String tampered = token + "x";
    assertThat(jwtService.validate(tampered)).isFalse();
  }

  private void setField(Object target, String name, Object value) throws Exception {
    Field field = JwtService.class.getDeclaredField(name);
    field.setAccessible(true);
    field.set(target, value);
  }
}
