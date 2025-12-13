package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.Enum.RegisterStatus;
import com.example.bookingbadminton.model.entity.RegisterOwner;

import java.util.List;
import java.util.UUID;

public interface RegisterOwnerService {
    List<RegisterOwner> findAll();

    RegisterOwner get(UUID id);

    RegisterOwner create(UUID accountId, String name, String address, String mobileContact, String gmail, RegisterStatus active, String linkMap);

    RegisterOwner update(UUID id, UUID accountId, String name, String address, String mobileContact, String gmail, RegisterStatus active, String linkMap);

    void delete(UUID id);
}
