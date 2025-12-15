package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.Enum.RegisterStatus;
import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.model.entity.RegisterOwner;
import com.example.bookingbadminton.repository.AccountRepository;
import com.example.bookingbadminton.repository.RegisterOwnerRepository;
import com.example.bookingbadminton.service.RegisterOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterOwnerServiceImpl implements RegisterOwnerService {

    private final RegisterOwnerRepository registerOwnerRepository;
    private final AccountRepository accountRepository;

    @Override
    public List<RegisterOwner> findAll() {
        return registerOwnerRepository.findAll();
    }

    @Override
    public RegisterOwner get(UUID id) {
        return registerOwnerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Register owner not found"));
    }

    @Override
    public RegisterOwner create(UUID accountId, String name, String address, String mobileContact, String gmail, RegisterStatus active, String linkMap) {
        RegisterOwner registerOwner = new RegisterOwner();
        return saveRegisterOwner(registerOwner, accountId, name, address, mobileContact, gmail, active, linkMap);
    }

    @Override
    public RegisterOwner update(UUID id, UUID accountId, String name, String address, String mobileContact, String gmail, RegisterStatus active, String linkMap) {
        RegisterOwner registerOwner = get(id);
        return saveRegisterOwner(registerOwner, accountId, name, address, mobileContact, gmail, active, linkMap);
    }

    private RegisterOwner saveRegisterOwner(RegisterOwner registerOwner, UUID accountId, String name, String address, String mobileContact, String gmail, RegisterStatus active, String linkMap) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        registerOwner.setAccount(account);
        registerOwner.setName(name);
        registerOwner.setAddress(address);
        registerOwner.setMobileContact(mobileContact);
        registerOwner.setGmail(gmail);
        registerOwner.setActive(active);
        registerOwner.setLinkMap(linkMap);
        return registerOwnerRepository.save(registerOwner);
    }

    @Override
    public void delete(UUID id) {
        RegisterOwner registerOwner = get(id);
        registerOwner.setDeletedAt(LocalDateTime.now());
        registerOwnerRepository.save(registerOwner);
    }
}
