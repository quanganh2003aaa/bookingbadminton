package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.entity.User;
import com.example.bookingbadminton.payload.UserRequest;
import com.example.bookingbadminton.payload.UserAdminResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<User> findAll();

    User get(UUID id);

    User create(UserRequest request);

    User update(UUID id, UserRequest request);

    Page<UserAdminResponse> adminList(String search, Boolean locked, Pageable pageable);
}
