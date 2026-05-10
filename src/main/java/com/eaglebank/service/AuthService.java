package com.eaglebank.service;

import com.eaglebank.dto.SetPasswordRequest;
import com.eaglebank.entity.PasswordSetupTokenEntity;
import com.eaglebank.entity.User;
import com.eaglebank.exception.BadRequestException;
import com.eaglebank.repository.PasswordSetupTokenRepository;
import com.eaglebank.repository.UserRepository;
import com.eaglebank.security.JwtService;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepo;
  private final PasswordEncoder encoder;
  private final JwtService jwtService;
  private final PasswordSetupTokenRepository tokenRepo;

  public String authenticateBasic(String authorization) {
    if (authorization == null || !authorization.startsWith("Basic ")) {
      throw new BadCredentialsException("Missing Basic auth header");
    }

    String base64Creds = authorization.substring(6).trim();
    String decoded;
    try {
      decoded = new String(Base64.getDecoder().decode(base64Creds), StandardCharsets.UTF_8);
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("Invalid Basic auth encoding");
    }

    int idx = decoded.indexOf(':');
    if (idx <= 0) {
      throw new BadRequestException("Invalid Basic auth format");
    }

    return authenticate(decoded.substring(0, idx), decoded.substring(idx + 1));
  }

  public String authenticate(String username, String password) {
    var user =
        userRepo
            .findByUsername(username)
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
            .findByTokenHashJoinUser(request.getToken())
            .orElseThrow(() -> new BadRequestException("Invalid token"));

    if (token.isUsed()) {
      throw new BadRequestException("Token already used");
    }

    if (token.getExpiresAt().isBefore(Instant.now())) {
      throw new BadRequestException("Token expired");
    }

    User user = token.getUser();

    if (user == null) {
      throw new BadRequestException("Token does not match user");
    }

    user.setUsername(request.getUsername());
    user.setPassword(encoder.encode(request.getPassword()));
    userRepo.save(user);

    token.setUsed(true);
    tokenRepo.save(token);
  }
}
