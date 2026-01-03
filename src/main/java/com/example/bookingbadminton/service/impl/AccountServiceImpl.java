package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.dto.AccountDTO;
import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.model.entity.Admin;
import com.example.bookingbadminton.model.entity.Owner;
import com.example.bookingbadminton.model.entity.User;
import com.example.bookingbadminton.payload.CreateAccountRequest;
import com.example.bookingbadminton.payload.request.LoginRequest;
import com.example.bookingbadminton.repository.AccountRepository;
import com.example.bookingbadminton.repository.AdminRepository;
import com.example.bookingbadminton.repository.OwnerRepository;
import com.example.bookingbadminton.repository.UserRepository;
import com.example.bookingbadminton.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.bookingbadminton.constant.VIMessage.ERROR_500;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
    private final AdminRepository adminRepository;

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account get(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    }

    @Override
    public Account create(CreateAccountRequest request) {
        if (accountRepository.existsByGmailIgnoreCase(request.gmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Gmail already exists");
        }
        Account account = new Account();
        account.setPassword(passwordEncoder.encode(request.password()));
        account.setGmail(request.gmail());
        account.setMsisdn(request.msisdn());
        return accountRepository.save(account);
    }

    @Override
    public Account update(UUID id, Account account) {
        Account existing = get(id);
        existing.setPassword(passwordEncoder.encode(account.getPassword()));
        existing.setGmail(account.getGmail());
        existing.setMsisdn(account.getMsisdn());
        return accountRepository.save(existing);
    }

    @Override
    public void delete(UUID id) {
        Account account = get(id);
        account.setDeletedAt(LocalDateTime.now());
        accountRepository.save(account);
    }

    @Override
    public String lock(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng!"));
        Account account = user.getAccount();
        if (account.getDeletedAt() == null) {
            account.setUpdatedAt(LocalDateTime.now());
            account.setDeletedAt(LocalDateTime.now());
            accountRepository.save(account);
        }
        return "Khóa tài khoản thành công";
    }

    @Override
    public String unlock(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng!"));
        Account account = user.getAccount();
        if (account.getDeletedAt() != null) {
            account.setUpdatedAt(LocalDateTime.now());
            account.setDeletedAt(null);
            accountRepository.save(account);
        }
        return "Mở khóa tài khoản thành công";
    }

    @Override
    public AccountDTO login(LoginRequest request) {
        Account account = checkAccountLogin(request.gmail(), request.password());
        User user = userRepository.findByAccount(account)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thông tin tài khoản không chính xác!"));

        return new AccountDTO(account, user);
    }

    @Override
    public AccountDTO loginOwner(LoginRequest request) {
        Account account = checkAccountLogin(request.gmail(), request.password());
        Owner owner = ownerRepository.findByAccount_Id(account.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thông tin tài khoản không chính xác!"));

        return new AccountDTO(account, owner);
    }

    @Override
    public AccountDTO loginAdmin(LoginRequest request) {
        Account account = checkAccountLogin(request.gmail(), request.password());
        Admin admin = adminRepository.findByAccount_Id(account.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tài khoản không chính xác!"));

        return new AccountDTO(account, admin);
    }

    private Account checkAccountLogin(String gmail, String password){
        Account account = accountRepository.findByGmailIgnoreCase(gmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Thông tin đăng nhập không chính xác!"));
        if (account.getDeletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Thông tin đăng nhập không chính xác!");
        }
        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Thông tin đăng nhập không chính xác!");
        }
        return account;
    }
}
