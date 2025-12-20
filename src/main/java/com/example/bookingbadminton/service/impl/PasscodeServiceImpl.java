package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.Enum.ActiveStatus;
import com.example.bookingbadminton.model.Enum.TypePasscode;
import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.model.entity.Passcode;
import com.example.bookingbadminton.payload.RegisterOwnerPasscodeRequest;
import com.example.bookingbadminton.payload.RegisterOwnerPasscodeResponse;
import com.example.bookingbadminton.repository.AccountRepository;
import com.example.bookingbadminton.repository.PasscodeRepository;
import com.example.bookingbadminton.service.AccountService;
import com.example.bookingbadminton.service.PasscodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PasscodeServiceImpl implements PasscodeService {

    private final PasscodeRepository passcodeRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Passcode> findAll() {
        return passcodeRepository.findAll();
    }

    @Override
    public Passcode get(UUID id) {
        return passcodeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Passcode not found"));
    }

    @Override
    public Passcode create(UUID accountId, String code, TypePasscode type) {
        if (passcodeRepository.findByAccount_Id(accountId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Passcode already exists");
        }
        Passcode passcode = new Passcode();
        return savePasscode(passcode, accountId, code, type, ActiveStatus.ACTIVE, LocalDateTime.now(), 0, 0);
    }

    @Override
    public Passcode update(UUID id, UUID accountId, String code, TypePasscode type, ActiveStatus active, LocalDateTime time, Integer totalDay, Integer totalMonth) {
        Passcode passcode = get(id);
        return savePasscode(passcode, accountId, code, type, active, time, totalDay, totalMonth);
    }

    private Passcode savePasscode(Passcode passcode, UUID accountId, String code, TypePasscode type, ActiveStatus active, LocalDateTime time, Integer totalDay, Integer totalMonth) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        passcode.setAccount(account);
        passcode.setCode(code);
        passcode.setType(type);
        passcode.setActive(active);
        passcode.setTime(time);
        passcode.setTotalDay(totalDay);
        passcode.setTotalMonth(totalMonth);
        return passcodeRepository.save(passcode);
    }

    @Override
    public void delete(UUID id) {
        if (!passcodeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Passcode not found");
        }
        passcodeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public RegisterOwnerPasscodeResponse createRegisterOwnerPasscode(RegisterOwnerPasscodeRequest request) {
        Account account = accountRepository.findByGmailIgnoreCase(request.account().gmail()).orElse(null);
        if (account != null) {
            if (account.getDeletedAt() != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account inactive");
            }
            if (!account.getMsisdn().equals(request.account().msisdn())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Msisdn does not match account");
            }
            if (!passwordEncoder.matches(request.account().password(), account.getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
            }
        } else {
            account = accountService.create(request.account());
        }

        Passcode passcode = passcodeRepository.findByAccount_Id(account.getId()).orElse(null);
        LocalDateTime now = LocalDateTime.now();
        if (passcode == null) {
            passcode = new Passcode();
            passcode.setAccount(account);
            passcode.setType(TypePasscode.REGISTER_OWNER_CODE);
            passcode.setActive(ActiveStatus.ACTIVE);
            passcode.setCode(generateCode());
            passcode.setTime(now);
            passcode.setTotalDay(1);
            passcode.setTotalMonth(1);
        } else {
            try {
                applyUsageLimits(passcode, now);
            } catch (ResponseStatusException ex) {
                passcodeRepository.save(passcode);
                throw ex;
            }
            passcode.setType(TypePasscode.REGISTER_OWNER_CODE);
            passcode.setActive(ActiveStatus.ACTIVE);
            passcode.setCode(generateCode());
            passcode.setTime(now);
        }
        Passcode saved = passcodeRepository.save(passcode);
        return new RegisterOwnerPasscodeResponse(account.getId(), saved.getId(), saved.getCode());
    }

    private void applyUsageLimits(Passcode passcode, LocalDateTime now) {
        if (passcode.getActive() == ActiveStatus.INACTIVE) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Passcode inactive");
        }
        LocalDateTime lastTime = passcode.getTime();
        int totalDay = passcode.getTotalDay() == null ? 0 : passcode.getTotalDay();
        int totalMonth = passcode.getTotalMonth() == null ? 0 : passcode.getTotalMonth();

        if (lastTime == null || !isSameDay(lastTime, now)) {
            totalDay = 0;
        }
        if (lastTime == null || !isSameMonth(lastTime, now)) {
            totalMonth = 0;
        }

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

    private String generateCode() {
        int code = ThreadLocalRandom.current().nextInt(0, 1_000_000);
        return String.format("%06d", code);
    }
}
