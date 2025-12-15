package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.payload.CreateAccountRequest;
import com.example.bookingbadminton.repository.AccountRepository;
import com.example.bookingbadminton.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account get(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    }

    @Override
    public Account create(CreateAccountRequest request) {
        if (accountRepository.existsByGmailIgnoreCase(request.gmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Gmail already exists");
        }
        if (accountRepository.existsByMsisdn(request.msisdn())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Msisdn already exists");
        }
        Account account = new Account();
        account.setPassword(passwordEncoder.encode(request.password()));
        account.setGmail(request.gmail());
        account.setMsisdn(request.msisdn());
        return accountRepository.save(account);
    }

    @Override
    public Account update(UUID id, Account account) {
        Account existing = get(id);
        existing.setPassword(passwordEncoder.encode(account.getPassword()));
        existing.setGmail(account.getGmail());
        existing.setMsisdn(account.getMsisdn());
        return accountRepository.save(existing);
    }

    @Override
    public void delete(UUID id) {
        Account account = get(id);
        account.setDeletedAt(LocalDateTime.now());
        accountRepository.save(account);
    }
}
