package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.entity.Field;
import com.example.bookingbadminton.model.entity.RegisterOwner;
import com.example.bookingbadminton.model.Enum.RegisterStatus;
import com.example.bookingbadminton.payload.RegisterOwnerConfirmRequest;
import com.example.bookingbadminton.payload.RegisterOwnerRequest;
import com.example.bookingbadminton.payload.RegisterOwnerResponse;
import com.example.bookingbadminton.payload.RegisterOwnerAdminResponse;
import com.example.bookingbadminton.payload.RegisterOwnerDetailResponse;
import com.example.bookingbadminton.payload.RegisterOwnerRejectResponse;

import java.util.List;
import java.util.UUID;

public interface RegisterOwnerService {
    List<RegisterOwner> findAll();

    RegisterOwner get(UUID id);

    RegisterOwner create(RegisterOwnerRequest request);

    RegisterOwner update(UUID id, RegisterOwnerRequest request);

    void delete(UUID id);

    RegisterOwnerResponse confirm(RegisterOwnerConfirmRequest request);

    Field approve(UUID id);

    List<RegisterOwnerAdminResponse> adminList(RegisterStatus status, String search);

    RegisterOwnerDetailResponse detail(UUID id);

    RegisterOwnerRejectResponse reject(UUID id);
}
