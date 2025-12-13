package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.model.entity.Admin;
import com.example.bookingbadminton.repository.AccountRepository;
import com.example.bookingbadminton.repository.AdminRepository;
import com.example.bookingbadminton.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final AccountRepository accountRepository;

    @Override
    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    @Override
    public Admin get(UUID id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));
    }

    @Override
    public Admin create(UUID accountId, String name) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        Admin admin = new Admin();
        admin.setAccount(account);
        admin.setName(name);
        return adminRepository.save(admin);
    }

    @Override
    public Admin update(UUID id, UUID accountId, String name) {
        Admin existing = get(id);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        existing.setAccount(account);
        existing.setName(name);
        return adminRepository.save(existing);
    }

    @Override
    public void delete(UUID id) {
        if (!adminRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found");
        }
        adminRepository.deleteById(id);
    }
}
