package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.model.entity.User;
import com.example.bookingbadminton.payload.UserAdminResponse;
import com.example.bookingbadminton.payload.UserRequest;
import com.example.bookingbadminton.repository.AccountRepository;
import com.example.bookingbadminton.repository.UserRepository;
import com.example.bookingbadminton.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
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
    public User create(UserRequest request) {
        return saveUser(new User(), request);
    }

    @Override
    public User update(UUID id, UserRequest request) {
        return saveUser(get(id), request);
    }

    private User saveUser(User user, UserRequest request) {
        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        user.setAccount(account);
        user.setName(request.name());
        user.setAvatar(request.avatar());
        return userRepository.save(user);
    }

    @Override
    public Page<UserAdminResponse> adminList(String search, Boolean locked, Pageable pageable) {
        return userRepository.findByFilters(search, locked, pageable)
                .map(u -> new UserAdminResponse(
                        u.getId(),
                        u.getAccount().getMsisdn(),
                        u.getAccount().getGmail(),
                        u.getName(),
                        u.getDeletedAt()
                ));
    }
}
