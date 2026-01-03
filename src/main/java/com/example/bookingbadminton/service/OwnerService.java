package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.entity.Owner;
import com.example.bookingbadminton.payload.OwnerRequest;
import com.example.bookingbadminton.payload.request.RegisterOwnerRequest;

import java.util.List;
import java.util.UUID;

public interface OwnerService {
    List<Owner> findAll();

    Owner get(UUID id);

    Owner create(RegisterOwnerRequest request);

    Owner update(UUID id, OwnerRequest request);

    void delete(UUID id);
}
