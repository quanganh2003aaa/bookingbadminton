package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.model.entity.User;
import com.example.bookingbadminton.repository.AccountRepository;
import com.example.bookingbadminton.repository.UserRepository;
import com.example.bookingbadminton.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User get(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Override
    public User create(UUID accountId, String name, String avatar) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        User user = new User();
        user.setAccount(account);
        user.setName(name);
        user.setAvatar(avatar);
        return userRepository.save(user);
    }

    @Override
    public User update(UUID id, UUID accountId, String name, String avatar) {
        User existing = get(id);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        existing.setAccount(account);
        existing.setName(name);
        existing.setAvatar(avatar);
        return userRepository.save(existing);
    }

    @Override
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteById(id);
    }
}
