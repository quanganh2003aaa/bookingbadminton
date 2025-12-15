package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.model.entity.Owner;
import com.example.bookingbadminton.payload.OwnerRequest;
import com.example.bookingbadminton.repository.AccountRepository;
import com.example.bookingbadminton.repository.OwnerRepository;
import com.example.bookingbadminton.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;
    private final AccountRepository accountRepository;

    @Override
    public List<Owner> findAll() {
        return ownerRepository.findAll();
    }

    @Override
    public Owner get(UUID id) {
        return ownerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found"));
    }

    @Override
    public Owner create(OwnerRequest request) {
        return saveOwner(new Owner(), request);
    }

    @Override
    public Owner update(UUID id, OwnerRequest request) {
        return saveOwner(get(id), request);
    }

    private Owner saveOwner(Owner owner, OwnerRequest request) {
        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        owner.setAccount(account);
        owner.setName(request.name());
        owner.setAvatar(request.avatar());
        return ownerRepository.save(owner);
    }

    @Override
    public void delete(UUID id) {
        if (!ownerRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found");
        }
        ownerRepository.deleteById(id);
    }
}
