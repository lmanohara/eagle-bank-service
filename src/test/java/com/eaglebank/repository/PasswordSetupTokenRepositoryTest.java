package com.eaglebank.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.eaglebank.entity.PasswordSetupTokenEntity;
import com.eaglebank.entity.User;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
public class PasswordSetupTokenRepositoryTest {

  @Autowired private PasswordSetupTokenRepository tokenRepository;
  @Autowired private UserRepository userRepository;

  @Test
  void findByTokenHashJoinUser_returnsEntityWithUser() {
    User user = User.builder().id("usr-test").name("T").email("t@example.com").build();
    userRepository.saveAndFlush(user);

    PasswordSetupTokenEntity token =
        PasswordSetupTokenEntity.builder()
            .user(user)
            .tokenHash("tok-123")
            .expiresAt(Instant.now().plusSeconds(3600))
            .used(false)
            .build();

    tokenRepository.save(token);

    var found = tokenRepository.findByTokenHashJoinUser("tok-123");

    assertThat(found).isPresent();
    assertThat(found.get().getUser()).isNotNull();
    assertThat(found.get().getUser().getId()).isEqualTo("usr-test");
  }
}
