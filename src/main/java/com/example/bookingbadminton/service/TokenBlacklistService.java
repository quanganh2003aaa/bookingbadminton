package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.entity.TokenBlacklist;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TokenBlacklistService {
    List<TokenBlacklist> findAll();

    TokenBlacklist get(UUID id);

    TokenBlacklist create(UUID accountId, UUID jit, LocalDateTime expiredAt);

    TokenBlacklist update(UUID id, UUID accountId, UUID jit, LocalDateTime expiredAt);

    void delete(UUID id);
}
