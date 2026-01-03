package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.dto.AccountDTO;
import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.payload.CreateAccountRequest;
import com.example.bookingbadminton.payload.request.LoginRequest;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    List<Account> findAll();

    Account get(UUID id);

    Account create(CreateAccountRequest request);

    Account update(UUID id, Account account);

    void delete(UUID id);

    AccountDTO login(LoginRequest request);

    AccountDTO loginOwner(LoginRequest request);

    AccountDTO loginAdmin(LoginRequest request);

    String lock(UUID id);

    String unlock(UUID id);
}
