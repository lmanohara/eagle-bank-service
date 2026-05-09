package com.eaglebank.service;

import static org.assertj.core.api.Assertions.*;

import com.eaglebank.entity.User;
import com.eaglebank.repository.PasswordSetupTokenRepository;
import com.eaglebank.repository.UserRepository;
import com.eaglebank.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

  @InjectMocks private AuthService authService;
  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private JwtService jwtService;
  @Mock private PasswordSetupTokenRepository passwordSetupTokenRepository;

  @Test
  void authenticate_success() {
    User user = User.builder().username("alice").password("hashed").build();

    Mockito.when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
    Mockito.when(passwordEncoder.matches("secret", "hashed")).thenReturn(true);
    Mockito.when(jwtService.generateToken("alice")).thenReturn("tok");

    String token = authService.authenticate("alice", "secret");
    assertThat(token).isEqualTo("tok");
  }

  @Test
  void authenticate_invalid() {
    Mockito.when(userRepository.findByUsername("bob")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.authenticate("bob", "pw"))
        .isInstanceOf(BadCredentialsException.class);
  }
}
