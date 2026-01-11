package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.entity.Field;
import com.example.bookingbadminton.model.entity.RegisterOwner;
import com.example.bookingbadminton.model.Enum.RegisterStatus;
import com.example.bookingbadminton.payload.RegisterOwnerAdminResponse;
import com.example.bookingbadminton.payload.RegisterOwnerDetailResponse;
import com.example.bookingbadminton.payload.RegisterOwnerRejectResponse;

import java.util.List;
import java.util.UUID;

public interface RegisterOwnerService {
    RegisterOwner get(UUID id);

    Field approve(UUID id);

    List<RegisterOwnerAdminResponse> adminList(RegisterStatus status, String search);

    RegisterOwnerDetailResponse detail(UUID id);

    RegisterOwnerRejectResponse reject(UUID id);
}
