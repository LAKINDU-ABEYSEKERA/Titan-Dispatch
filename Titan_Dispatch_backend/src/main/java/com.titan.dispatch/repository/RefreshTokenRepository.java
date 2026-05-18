package com.titan.dispatch.repository;

import com.titan.dispatch.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);

    // UPDATED to standard Spring Data naming convention
    void deleteByUserId(UUID userId);
}