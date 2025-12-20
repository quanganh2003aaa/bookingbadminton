package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.model.entity.RefreshToken;
import com.example.bookingbadminton.repository.AccountRepository;
import com.example.bookingbadminton.repository.RefreshTokenRepository;
import com.example.bookingbadminton.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AccountRepository accountRepository;

    @Override
    public List<RefreshToken> findAll() {
        return refreshTokenRepository.findAll();
    }

    @Override
    public RefreshToken get(UUID id) {
        return refreshTokenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Refresh token not found"));
    }

    @Override
    public RefreshToken create(UUID accountId, String hashToken, String refreshToken, LocalDateTime expiredAt, Boolean revoked, LocalDateTime revokedAt) {
        RefreshToken token = new RefreshToken();
        return saveToken(token, accountId, hashToken, refreshToken, expiredAt, revoked, revokedAt);
    }

    @Override
    public RefreshToken update(UUID id, UUID accountId, String hashToken, String refreshToken, LocalDateTime expiredAt, Boolean revoked, LocalDateTime revokedAt) {
        RefreshToken token = get(id);
        return saveToken(token, accountId, hashToken, refreshToken, expiredAt, revoked, revokedAt);
    }

    private RefreshToken saveToken(RefreshToken token, UUID accountId, String hashToken, String refreshToken, LocalDateTime expiredAt, Boolean revoked, LocalDateTime revokedAt) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        token.setAccount(account);
        token.setHashToken(hashToken);
        token.setRefreshToken(refreshToken);
        token.setExpiredAt(expiredAt);
        token.setRevoked(revoked);
        token.setRevokedAt(revokedAt);
        return refreshTokenRepository.save(token);
    }

    @Override
    public void delete(UUID id) {
        if (!refreshTokenRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Refresh token not found");
        }
        refreshTokenRepository.deleteById(id);
    }
}
