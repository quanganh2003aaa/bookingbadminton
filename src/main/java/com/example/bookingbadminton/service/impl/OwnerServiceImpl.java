package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.model.entity.Owner;
import com.example.bookingbadminton.payload.DetailInfoOwnerResp;
import com.example.bookingbadminton.payload.OwnerRequest;
import com.example.bookingbadminton.payload.request.RegisterOwnerRequest;
import com.example.bookingbadminton.repository.AccountRepository;
import com.example.bookingbadminton.repository.OwnerRepository;
import com.example.bookingbadminton.service.AuthenticationService;
import com.example.bookingbadminton.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.bookingbadminton.constant.Const.AVATAR_DEFAULT;

@Service
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;
    private final AccountRepository accountRepository;
    private final AuthenticationService authenticationService;

    private Owner saveOwner(Owner owner, OwnerRequest request) {
        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        owner.setAccount(account);
        owner.setName(request.name());
        owner.setAvatar(request.avatar());
        return ownerRepository.save(owner);
    }

    @Override
    public DetailInfoOwnerResp getDetailInfoOwner(UUID ownerId) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy owner"));

        return DetailInfoOwnerResp.builder()
                .nameOwner(owner.getName())
                .email(owner.getAccount().getGmail())
                .msisdn(owner.getAccount().getMsisdn())
                .avatar(owner.getAvatar())
                .build();
    }
}
