package com.eaglebank.repository;

import com.eaglebank.entity.PasswordSetupTokenEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PasswordSetupTokenRepository
    extends JpaRepository<PasswordSetupTokenEntity, UUID> {
  Optional<PasswordSetupTokenEntity> findByTokenHash(String tokenHash);

  @Query(
      """
SELECT token FROM PasswordSetupTokenEntity token
JOIN FETCH token.user
WHERE token.tokenHash = :tokenHash
""")
  Optional<PasswordSetupTokenEntity> findByTokenHashJoinUser(String tokenHash);
}
