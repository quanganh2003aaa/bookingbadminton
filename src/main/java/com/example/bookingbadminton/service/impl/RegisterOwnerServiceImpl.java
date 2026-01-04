package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.Enum.ActiveStatus;
import com.example.bookingbadminton.model.Enum.RegisterStatus;
import com.example.bookingbadminton.model.Enum.TypePasscode;
import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.model.entity.Field;
import com.example.bookingbadminton.model.entity.Owner;
import com.example.bookingbadminton.model.entity.Passcode;
import com.example.bookingbadminton.model.entity.RegisterOwner;
import com.example.bookingbadminton.payload.RegisterOwnerAdminResponse;
import com.example.bookingbadminton.payload.RegisterOwnerConfirmRequest;
import com.example.bookingbadminton.payload.RegisterOwnerDraft;
import com.example.bookingbadminton.payload.RegisterOwnerRequest;
import com.example.bookingbadminton.payload.RegisterOwnerResponse;
import com.example.bookingbadminton.payload.RegisterOwnerDetailResponse;
import com.example.bookingbadminton.payload.RegisterOwnerRejectResponse;
import com.example.bookingbadminton.repository.AccountRepository;
import com.example.bookingbadminton.repository.FieldRepository;
import com.example.bookingbadminton.repository.OwnerRepository;
import com.example.bookingbadminton.repository.PasscodeRepository;
import com.example.bookingbadminton.repository.RegisterOwnerRepository;
import com.example.bookingbadminton.service.FieldImageService;
import com.example.bookingbadminton.model.Enum.TypeImage;
import com.example.bookingbadminton.service.RegisterOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterOwnerServiceImpl implements RegisterOwnerService {

    private final RegisterOwnerRepository registerOwnerRepository;
    private final AccountRepository accountRepository;
    private final PasscodeRepository passcodeRepository;
    private final OwnerRepository ownerRepository;
    private final FieldRepository fieldRepository;
    private final FieldImageService fieldImageService;

    @Override
    public List<RegisterOwner> findAll() {
        return registerOwnerRepository.findAll();
    }

    @Override
    public RegisterOwner get(UUID id) {
        return registerOwnerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn đăng ký!"));
    }

    @Override
    public RegisterOwner create(RegisterOwnerRequest request) {
        return saveRegisterOwner(new RegisterOwner(), request);
    }

    @Override
    public RegisterOwner update(UUID id, RegisterOwnerRequest request) {
        return saveRegisterOwner(get(id), request);
    }

    private RegisterOwner saveRegisterOwner(RegisterOwner registerOwner, RegisterOwnerRequest request) {
        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        registerOwner.setAccount(account);
        registerOwner.setName(request.name());
        registerOwner.setAddress(request.address());
        registerOwner.setMobileContact(request.mobileContact());
        registerOwner.setGmail(request.gmail());
        registerOwner.setActive(request.active());
        registerOwner.setLinkMap(request.linkMap());
        registerOwner.setImgQr(request.imgQr());
        return registerOwnerRepository.save(registerOwner);
    }

    @Override
    public void delete(UUID id) {
        RegisterOwner registerOwner = get(id);
        registerOwner.setDeletedAt(LocalDateTime.now());
        registerOwnerRepository.save(registerOwner);
    }

    @Override
    public RegisterOwnerResponse confirm(RegisterOwnerConfirmRequest request) {
        Passcode passcode = passcodeRepository.findByAccount_Id(request.accountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passcode not found"));
        if (passcode.getType() != TypePasscode.REGISTER_OWNER_CODE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passcode type invalid");
        }
        LocalDateTime now = LocalDateTime.now();
        if (isExpired(passcode, now)) {
            passcode.setActive(ActiveStatus.INACTIVE);
            passcodeRepository.save(passcode);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passcode expired");
        }
        try {
            applyUsageLimits(passcode, now);
        } catch (ResponseStatusException ex) {
            passcodeRepository.save(passcode);
            throw ex;
        }
        if (!passcode.getCode().equals(request.code())) {
            passcodeRepository.save(passcode);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passcode invalid");
        }
        passcode.setActive(ActiveStatus.INACTIVE);
        passcodeRepository.save(passcode);

        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        RegisterOwnerDraft draft = request.register();

        // Ensure owner record exists for this account
        ownerRepository.findByAccount_Id(account.getId()).or(() -> {
            Owner owner = new Owner();
            owner.setAccount(account);
            owner.setName(draft.name());
            owner.setAvatar(null);
            return Optional.of(ownerRepository.save(owner));
        });

        RegisterOwner registerOwner = new RegisterOwner();
        registerOwner.setAccount(account);
        registerOwner.setName(draft.name());
        registerOwner.setAddress(draft.address());
        registerOwner.setMobileContact(draft.mobileContact());
        registerOwner.setGmail(account.getGmail());
        registerOwner.setActive(RegisterStatus.INACCEPT);
        registerOwner.setLinkMap(draft.linkMap());
        registerOwner.setImgQr(draft.imgQr());
        RegisterOwner saved = registerOwnerRepository.save(registerOwner);
        return new RegisterOwnerResponse(
                saved.getId(),
                account.getId(),
                saved.getName(),
                saved.getAddress(),
                saved.getMobileContact(),
                saved.getGmail(),
                saved.getActive(),
                saved.getLinkMap(),
                saved.getImgQr()
        );
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
        Field savedField = fieldRepository.save(field);
        if (registerOwner.getImgQr() != null) {
            fieldImageService.create(savedField.getId(), TypeImage.QR, registerOwner.getImgQr());
        }

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

    private void applyUsageLimits(Passcode passcode, LocalDateTime now) {
        LocalDateTime lastTime = passcode.getTime();
        int totalDay = passcode.getTotalDay() == null ? 0 : passcode.getTotalDay();
        int totalMonth = passcode.getTotalMonth() == null ? 0 : passcode.getTotalMonth();

        if (lastTime == null || !isSameDay(lastTime, now)) {
            totalDay = 0;
        }
        if (lastTime == null || !isSameMonth(lastTime, now)) {
            totalMonth = 0;
        }

        // reset trạng thái khi bước sang ngày/tháng mới
        passcode.setActive(ActiveStatus.ACTIVE);

        if (totalDay >= 5) {
            passcode.setActive(ActiveStatus.INACTIVE);
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Passcode daily limit exceeded");
        }
        if (totalMonth >= 15) {
            passcode.setActive(ActiveStatus.INACTIVE);
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Passcode monthly limit exceeded");
        }

        totalDay++;
        totalMonth++;
        passcode.setTotalDay(totalDay);
        passcode.setTotalMonth(totalMonth);
        passcode.setTime(now);

        if (totalDay > 5 || totalMonth > 15) {
            passcode.setActive(ActiveStatus.INACTIVE);
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Passcode limit exceeded");
        }
    }

    private boolean isSameDay(LocalDateTime a, LocalDateTime b) {
        return a.toLocalDate().isEqual(b.toLocalDate());
    }

    private boolean isSameMonth(LocalDateTime a, LocalDateTime b) {
        return a.getYear() == b.getYear() && a.getMonth() == b.getMonth();
    }

    private boolean isExpired(Passcode passcode, LocalDateTime now) {
        LocalDateTime createdTime = passcode.getTime();
        return createdTime == null || now.isAfter(createdTime.plusSeconds(60));
    }

}
