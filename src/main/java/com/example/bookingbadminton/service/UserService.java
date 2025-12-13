package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<User> findAll();

    User get(UUID id);

    User create(UUID accountId, String name, String avatar);

    User update(UUID id, UUID accountId, String name, String avatar);

    void delete(UUID id);
}
