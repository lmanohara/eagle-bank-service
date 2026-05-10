package com.eaglebank.service;

import com.eaglebank.dto.AddressDto;
import com.eaglebank.dto.UserRequest;
import com.eaglebank.dto.UserResponse;
import com.eaglebank.entity.Address;
import com.eaglebank.entity.PasswordSetupTokenEntity;
import com.eaglebank.entity.User;
import com.eaglebank.exception.BadRequestException;
import com.eaglebank.repository.PasswordSetupTokenRepository;
import com.eaglebank.repository.UserRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository repo;
  private final PasswordSetupTokenRepository tokenRepo;

  public UserResponse createUser(UserRequest userRequest) {
    if (userRequest.getEmail() == null) {
      throw new BadRequestException("Email is required");
    }

    if (repo.findByEmail(userRequest.getEmail()).isPresent()) {
      throw new BadRequestException("Email exists");
    }

    User user = saveUser(userRequest);

    String token = generatePasswordSetupToken(user);

    return mapToUserResponse(user, token);
  }

  private User saveUser(UserRequest req) {
    Address addr = null;
    if (req.getAddress() != null) {
      AddressDto address = req.getAddress();
      addr =
          Address.builder()
              .line1(address.getLine1())
              .line2(address.getLine2())
              .line3(address.getLine3())
              .town(address.getTown())
              .county(address.getCounty())
              .postcode(address.getPostcode())
              .build();
    }

    User user =
        User.builder()
            .id("usr-" + UUID.randomUUID())
            .name(req.getName())
            .email(req.getEmail())
            .phoneNumber(req.getPhoneNumber())
            .address(addr)
            .createdTimestamp(Instant.now())
            .updatedTimestamp(Instant.now())
            .build();

    repo.save(user);
    return user;
  }

  private UserResponse mapToUserResponse(User user, String token) {
    AddressDto address = null;
    if (user.getAddress() != null) {
      address =
          AddressDto.builder()
              .line1(user.getAddress().getLine1())
              .line2(user.getAddress().getLine2())
              .line3(user.getAddress().getLine3())
              .town(user.getAddress().getTown())
              .county(user.getAddress().getCounty())
              .postcode(user.getAddress().getPostcode())
              .build();
    }

    return UserResponse.builder()
        .id(user.getId())
        .name(user.getName())
        .address(address)
        .phoneNumber(user.getPhoneNumber())
        .email(user.getEmail())
        .createdTimestamp(user.getCreatedTimestamp())
        .updatedTimestamp(user.getUpdatedTimestamp())
        .passwordSetupToken(token)
        .build();
  }

  public UserResponse getUserById(String id, String authUsername) {
    User user = repo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

    if (user.getUsername() == null || !user.getUsername().equals(authUsername)) {
      throw new RuntimeException("Forbidden");
    }

    return mapToUserResponse(user, null);
  }

  // TODO: 09/05/2026 this should move to separate service
  private String generatePasswordSetupToken(User user) {
    String token = UUID.randomUUID().toString();
    PasswordSetupTokenEntity passwordSetupTokenEntity =
        PasswordSetupTokenEntity.builder()
            .user(user)
            .tokenHash(token)
            .expiresAt(Instant.now().plus(Duration.ofDays(1)))
            .used(false)
            .build();

    tokenRepo.save(passwordSetupTokenEntity);
    return token;
  }
}
