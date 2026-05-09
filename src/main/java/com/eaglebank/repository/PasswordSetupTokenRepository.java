package com.eaglebank.repository;

import com.eaglebank.entity.PasswordSetupTokenEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordSetupTokenRepository
    extends JpaRepository<PasswordSetupTokenEntity, UUID> {
  Optional<PasswordSetupTokenEntity> findByTokenHash(String tokenHash);
}
