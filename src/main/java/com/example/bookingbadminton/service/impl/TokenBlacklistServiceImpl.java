package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.model.entity.TokenBlacklist;
import com.example.bookingbadminton.repository.AccountRepository;
import com.example.bookingbadminton.repository.TokenBlacklistRepository;
import com.example.bookingbadminton.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final AccountRepository accountRepository;

    @Override
    public List<TokenBlacklist> findAll() {
        return tokenBlacklistRepository.findAll();
    }

    @Override
    public TokenBlacklist get(UUID id) {
        return tokenBlacklistRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Token blacklist entry not found"));
    }

    @Override
    public TokenBlacklist create(UUID accountId, UUID jit, LocalDateTime expiredAt) {
        TokenBlacklist token = new TokenBlacklist();
        return saveToken(token, accountId, jit, expiredAt);
    }

    @Override
    public TokenBlacklist update(UUID id, UUID accountId, UUID jit, LocalDateTime expiredAt) {
        TokenBlacklist token = get(id);
        return saveToken(token, accountId, jit, expiredAt);
    }

    private TokenBlacklist saveToken(TokenBlacklist token, UUID accountId, UUID jit, LocalDateTime expiredAt) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        token.setAccount(account);
        token.setJit(jit);
        token.setExpiredAt(expiredAt);
        return tokenBlacklistRepository.save(token);
    }

    @Override
    public void delete(UUID id) {
        if (!tokenBlacklistRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Token blacklist entry not found");
        }
        tokenBlacklistRepository.deleteById(id);
    }
}
