package com.eaglebank.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import com.eaglebank.dto.AddressDto;
import com.eaglebank.dto.UserRequest;
import com.eaglebank.dto.UserResponse;
import com.eaglebank.entity.User;
import com.eaglebank.repository.PasswordSetupTokenRepository;
import com.eaglebank.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @InjectMocks private UserService userService;
  @Mock private UserRepository userRepository;
  @Mock private PasswordSetupTokenRepository passwordSetupTokenRepository;

  @Test
  void createUser_success_setsFieldsAndTimestamps() {
    UserRequest req = new UserRequest();
    req.setName("Test User");
    AddressDto a = new AddressDto();
    a.setLine1("l1");
    a.setLine2("l2");
    a.setLine3("l3");
    a.setTown("town");
    a.setCounty("county");
    a.setPostcode("pc");
    req.setAddress(a);
    req.setPhoneNumber("12345");
    req.setEmail("user@example.com");

    Mockito.when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
    Mockito.when(userRepository.save(any(User.class)))
        .thenAnswer(
            invocation -> {
              User u = invocation.getArgument(0);
              u.setId("usr-" + UUID.randomUUID());
              return u;
            });

    UserResponse userResponse = userService.createUser(req);

    Mockito.verify(passwordSetupTokenRepository).save(any());
    assertThat(userResponse.getId()).isNotNull();
    assertThat(userResponse.getName()).isEqualTo("Test User");
    assertThat(userResponse.getEmail()).isEqualTo("user@example.com");
    assertThat(userResponse.getPhoneNumber()).isEqualTo("12345");
    assertThat(userResponse.getCreatedTimestamp()).isNotNull();
    assertThat(userResponse.getUpdatedTimestamp()).isNotNull();
  }

  @Test
  void createUser_failsWhenEmailMissing() {
    UserRequest req = new UserRequest();
    req.setName("No Email");

    assertThatThrownBy(() -> userService.createUser(req)).isInstanceOf(RuntimeException.class);
  }
}
