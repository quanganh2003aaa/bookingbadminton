package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.dto.AccountDTO;
import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.model.entity.Owner;
import com.example.bookingbadminton.model.entity.User;
import com.example.bookingbadminton.payload.CreateAccountRequest;
import com.example.bookingbadminton.payload.LoginRequest;
import com.example.bookingbadminton.repository.AccountRepository;
import com.example.bookingbadminton.repository.OwnerRepository;
import com.example.bookingbadminton.repository.UserRepository;
import com.example.bookingbadminton.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.bookingbadminton.constant.VIMessage.ERROR_500;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;

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

    @Override
    public Account lock(UUID id) {
        com.example.bookingbadminton.model.entity.User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Account account = user.getAccount();
        if (account.getDeletedAt() == null) {
            account.setDeletedAt(LocalDateTime.now());
            accountRepository.save(account);
        }
        return account;
    }

    @Override
    public Account unlock(UUID id) {
        com.example.bookingbadminton.model.entity.User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Account account = user.getAccount();
        if (account.getDeletedAt() != null) {
            account.setDeletedAt(null);
            accountRepository.save(account);
        }
        return account;
    }

    @Override
    public AccountDTO login(LoginRequest request) {
        Account account = accountRepository.findByGmailIgnoreCase(request.gmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (account.getDeletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account inactive");
        }
        if (!passwordEncoder.matches(request.password(), account.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        User user = userRepository.findByAccount(account)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_500));

        return new AccountDTO(account, user);
    }

    @Override
    public AccountDTO loginOwner(LoginRequest request) {
        Account account = accountRepository.findByGmailIgnoreCase(request.gmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (account.getDeletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account inactive");
        }
        if (!passwordEncoder.matches(request.password(), account.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        Owner owner = ownerRepository.findByAccount_Id(account.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found"));

        return new AccountDTO(account, owner);
    }
}
