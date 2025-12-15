package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.entity.Owner;
import com.example.bookingbadminton.payload.OwnerRequest;

import java.util.List;
import java.util.UUID;

public interface OwnerService {
    List<Owner> findAll();

    Owner get(UUID id);

    Owner create(OwnerRequest request);

    Owner update(UUID id, OwnerRequest request);

    void delete(UUID id);
}
