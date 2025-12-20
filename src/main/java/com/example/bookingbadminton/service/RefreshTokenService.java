package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.entity.RefreshToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RefreshTokenService {
    List<RefreshToken> findAll();

    RefreshToken get(UUID id);

    RefreshToken create(UUID accountId, String hashToken, String refreshToken, LocalDateTime expiredAt, Boolean revoked, LocalDateTime revokedAt);

    RefreshToken update(UUID id, UUID accountId, String hashToken, String refreshToken, LocalDateTime expiredAt, Boolean revoked, LocalDateTime revokedAt);

    void delete(UUID id);
}
