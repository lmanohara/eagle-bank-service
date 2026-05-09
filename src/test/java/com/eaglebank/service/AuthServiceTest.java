package com.eaglebank.service;

import static org.assertj.core.api.Assertions.*;

import com.eaglebank.entity.User;
import com.eaglebank.repository.UserRepository;
import com.eaglebank.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthServiceTest {

  @Test
  void authenticate_success() {
    UserRepository repo = Mockito.mock(UserRepository.class);
    PasswordEncoder encoder = Mockito.mock(PasswordEncoder.class);
    JwtService jwtService = Mockito.mock(JwtService.class);

    AuthService authService = new AuthService(repo, encoder, jwtService);

    User user = User.builder().username("alice").password("hashed").build();

    Mockito.when(repo.findByUsername("alice")).thenReturn(Optional.of(user));
    Mockito.when(encoder.matches("secret", "hashed")).thenReturn(true);
    Mockito.when(jwtService.generateToken("alice")).thenReturn("tok");

    String token = authService.authenticate("alice", "secret");
    assertThat(token).isEqualTo("tok");
  }

  @Test
  void authenticate_invalid() {
    UserRepository repo = Mockito.mock(UserRepository.class);
    PasswordEncoder encoder = Mockito.mock(PasswordEncoder.class);
    JwtService jwtService = Mockito.mock(JwtService.class);

    AuthService authService = new AuthService(repo, encoder, jwtService);

    Mockito.when(repo.findByUsername("bob")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.authenticate("bob", "pw"))
        .isInstanceOf(BadCredentialsException.class);
  }
}
