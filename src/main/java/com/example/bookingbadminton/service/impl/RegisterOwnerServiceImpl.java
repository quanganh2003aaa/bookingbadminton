package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.Enum.ActiveStatus;
import com.example.bookingbadminton.model.Enum.RegisterStatus;
import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.model.entity.Field;
import com.example.bookingbadminton.model.entity.Owner;
import com.example.bookingbadminton.model.entity.RegisterOwner;
import com.example.bookingbadminton.payload.RegisterOwnerAdminResponse;
import com.example.bookingbadminton.payload.RegisterOwnerDetailResponse;
import com.example.bookingbadminton.payload.RegisterOwnerRejectResponse;
import com.example.bookingbadminton.repository.FieldRepository;
import com.example.bookingbadminton.repository.OwnerRepository;
import com.example.bookingbadminton.repository.RegisterOwnerRepository;
import com.example.bookingbadminton.service.RegisterOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterOwnerServiceImpl implements RegisterOwnerService {

    private final RegisterOwnerRepository registerOwnerRepository;
    private final OwnerRepository ownerRepository;
    private final FieldRepository fieldRepository;

    @Override
    public RegisterOwner get(UUID id) {
        return registerOwnerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn đăng ký!"));
    }

    @Override
    public List<RegisterOwnerAdminResponse> adminList(RegisterStatus status, String search) {
        List<RegisterOwner> list = registerOwnerRepository.findByFilters(status, search);
        return list.stream()
                .map(r -> new RegisterOwnerAdminResponse(
                        r.getId(),
                        r.getName(),
                        r.getGmail(),
                        r.getActive()
                ))
                .toList();
    }

    @Override
    public RegisterOwnerDetailResponse detail(UUID id) {
        RegisterOwner r = get(id);
        return new RegisterOwnerDetailResponse(
                r.getId(),
                r.getAccount().getId(),
                r.getName(),
                r.getAddress(),
                r.getMobileContact(),
                r.getGmail(),
                r.getActive(),
                r.getLinkMap(),
                r.getImgQr(),
                r.getCreatedAt(),
                r.getUpdatedAt(),
                r.getDeletedAt()
        );
    }

    @Override
    public Field approve(UUID id) {
        RegisterOwner registerOwner = get(id);
        if (registerOwner.getDeletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đơn đăng ký đã bị xóa!");
        }
        if (registerOwner.getActive() == RegisterStatus.ACCEPT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Đơn đăng ký đã được chấp thuận trước đó!");
        }

        Account account = registerOwner.getAccount();
        Owner owner = ownerRepository.findByAccount_Id(account.getId()).orElseGet(() -> {
            Owner o = new Owner();
            o.setAccount(account);
            o.setName(registerOwner.getName());
            o.setAvatar(null);
            return ownerRepository.save(o);
        });

        Field field = new Field();
        field.setOwner(owner);
        field.setName(registerOwner.getName());
        field.setAddress(registerOwner.getAddress());
        field.setRatePoint(null);
        field.setMsisdn(registerOwner.getMobileContact());
        field.setMobileContact(registerOwner.getMobileContact());
        field.setStartTime(null);
        field.setEndTime(null);
        field.setActive(ActiveStatus.INACTIVE);
        field.setLinkMap(registerOwner.getLinkMap());
        field.setIndexField(0);
        field.setQuantity(0);
        field.setImgQr(registerOwner.getImgQr());
        Field savedField = fieldRepository.save(field);

        registerOwner.setActive(RegisterStatus.ACCEPT);
        registerOwnerRepository.save(registerOwner);

        return savedField;
    }

    @Override
    public RegisterOwnerRejectResponse reject(UUID id) {
        RegisterOwner registerOwner = get(id);
        if (registerOwner.getDeletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đơn đăng ký đã bị xóa!");
        }
        if (registerOwner.getActive() == RegisterStatus.ACCEPT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Đơn đăng ký đã được chấp thuận trước đó!");
        }
        registerOwner.setActive(RegisterStatus.INACCEPT);
        registerOwnerRepository.save(registerOwner);
        return new RegisterOwnerRejectResponse(registerOwner.getId(), registerOwner.getActive());
    }

}
