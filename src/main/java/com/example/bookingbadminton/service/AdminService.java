package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.entity.Admin;

import java.util.List;
import java.util.UUID;

public interface AdminService {
    List<Admin> findAll();

    Admin get(UUID id);

    Admin create(UUID accountId, String name);

    Admin update(UUID id, UUID accountId, String name);

    void delete(UUID id);
}
