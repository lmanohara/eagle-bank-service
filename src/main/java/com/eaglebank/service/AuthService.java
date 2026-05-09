package com.eaglebank.service;

import com.eaglebank.repository.UserRepository;
import com.eaglebank.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository repo;
  private final PasswordEncoder encoder;
  private final JwtService jwtService;

  public String authenticate(String username, String password) {
    var user =
        repo.findByUsername(username)
            .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

    if (!encoder.matches(password, user.getPassword())) {
      throw new BadCredentialsException("Invalid username or password");
    }

    return jwtService.generateToken(user.getUsername());
  }
}
