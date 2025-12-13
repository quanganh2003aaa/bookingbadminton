package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.Enum.ActiveStatus;
import com.example.bookingbadminton.model.Enum.TypePasscode;
import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.model.entity.Passcode;
import com.example.bookingbadminton.repository.AccountRepository;
import com.example.bookingbadminton.repository.PasscodeRepository;
import com.example.bookingbadminton.service.PasscodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasscodeServiceImpl implements PasscodeService {

    private final PasscodeRepository passcodeRepository;
    private final AccountRepository accountRepository;

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
    public Passcode create(UUID accountId, String code, TypePasscode type, ActiveStatus active, LocalDateTime time, Integer totalDay, Integer totalMonth) {
        Passcode passcode = new Passcode();
        return savePasscode(passcode, accountId, code, type, active, time, totalDay, totalMonth);
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
}
