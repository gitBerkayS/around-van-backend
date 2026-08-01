package com.aroundvan.backend.auth;

import com.aroundvan.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface AuthEmailTokenRepository extends JpaRepository<AuthEmailToken, Long> {

    Optional<AuthEmailToken> findByTokenHashAndTypeAndUsedAtIsNull(
            String tokenHash,
            AuthEmailTokenType type
    );

    Optional<AuthEmailToken> findFirstByUserAndTypeAndUsedAtIsNullOrderByCreatedAtDesc(
            User user,
            AuthEmailTokenType type
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE AuthEmailToken t
            SET t.usedAt = :usedAt
            WHERE t.user = :user
              AND t.type = :type
              AND t.usedAt IS NULL
            """)
    void invalidateActiveTokens(
            @Param("user") User user,
            @Param("type") AuthEmailTokenType type,
            @Param("usedAt") Instant usedAt
    );
}
