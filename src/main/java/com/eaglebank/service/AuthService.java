package com.eaglebank.service;

import com.eaglebank.dto.SetPasswordRequest;
import com.eaglebank.entity.PasswordSetupTokenEntity;
import com.eaglebank.entity.User;
import com.eaglebank.repository.PasswordSetupTokenRepository;
import com.eaglebank.repository.UserRepository;
import com.eaglebank.security.JwtService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository repo;
  private final PasswordEncoder encoder;
  private final JwtService jwtService;
  private final PasswordSetupTokenRepository tokenRepo;

  public String authenticate(String username, String password) {
    var user =
        repo.findByUsername(username)
            .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

    if (!encoder.matches(password, user.getPassword())) {
      throw new BadCredentialsException("Invalid username or password");
    }

    return jwtService.generateToken(user.getUsername());
  }

  @Transactional
  public void setPassword(SetPasswordRequest request) {
    PasswordSetupTokenEntity token =
        tokenRepo
            .findByTokenHash(request.getToken())
            .orElseThrow(() -> new BadCredentialsException("Invalid token"));

    if (token.isUsed()) {
      throw new BadCredentialsException("Token already used");
    }

    if (token.getExpiresAt().isBefore(Instant.now())) {
      throw new BadCredentialsException("Token expired");
    }

    User user = token.getUser();
    if (user == null
        || user.getUsername() == null
        || !user.getUsername().equals(request.getUsername())) {
      throw new BadCredentialsException("Token does not match user");
    }

    user.setPassword(encoder.encode(request.getPassword()));
    repo.save(user);

    token.setUsed(true);
    tokenRepo.save(token);
  }
}
