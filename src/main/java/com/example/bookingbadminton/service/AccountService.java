package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.payload.CreateAccountRequest;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    List<Account> findAll();

    Account get(UUID id);

    Account create(CreateAccountRequest request);

    Account update(UUID id, Account account);

    void delete(UUID id);
}
