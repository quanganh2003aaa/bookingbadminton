package com.example.bookingbadminton.util;

import com.example.bookingbadminton.constant.ErrorMessage;
import com.example.bookingbadminton.exception.ResourceNotFoundException;
import com.example.bookingbadminton.model.Enum.ActiveStatus;
import com.example.bookingbadminton.model.Enum.TypePasscode;
import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.model.entity.Passcode;
import com.example.bookingbadminton.repository.AccountRepository;
import com.example.bookingbadminton.repository.PasscodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@RequiredArgsConstructor
public final class OtpUtil {

    private final PasscodeRepository passcodeRepository;
    private final AccountRepository accountRepository;

    private static final SecureRandom random = new SecureRandom();

    public static String generateOtp() {
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public void hasValidBeforeHasOtp(String gmail) {
        Account account = accountRepository.findByGmailIgnoreCase(gmail).orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.User.ERR_EMAIL_NOT_EXISTED));
        Passcode passcode = passcodeRepository.findByAccount_Id(account.getId()).orElseGet(null);
        LocalDateTime now = LocalDateTime.now();
        if (passcode == null) {
            passcode = new Passcode();
            passcode.setAccount(account);
            passcode.setType(TypePasscode.REGISTER_OWNER_CODE);
            passcode.setActive(ActiveStatus.ACTIVE);
            passcode.setCode(generateOtp());
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
            passcode.setCode(generateOtp());
            passcode.setTime(now);
        }
    }

    public void hasValidAfterHasOtp(Passcode passcode, String otpNeedVerify) {
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
        if (!passcode.getCode().equals(otpNeedVerify)) {
            passcodeRepository.save(passcode);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passcode invalid");
        }
    }

    private OtpUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
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
