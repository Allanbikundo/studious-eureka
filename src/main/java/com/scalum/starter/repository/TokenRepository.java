package com.scalum.starter.repository;

import com.scalum.starter.model.Token;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);

    @Modifying
    @Query(
            "DELETE FROM Token t WHERE (t.usedAt IS NOT NULL AND t.usedAt < :cutoff) OR (t.usedAt IS NULL AND t.expiresAt < :cutoff)")
    void deleteOldTokens(LocalDateTime cutoff);
}
