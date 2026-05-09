package com.eaglebank.repository;

import com.eaglebank.entity.PasswordSetupTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PasswordSetupTokenRepository extends JpaRepository<PasswordSetupTokenEntity, UUID> {
    Optional<PasswordSetupTokenEntity> findByTokenHash(String tokenHash);
}