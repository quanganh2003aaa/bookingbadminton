package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.entity.Field;
import com.example.bookingbadminton.payload.FieldAdminResponse;
import com.example.bookingbadminton.payload.FieldCardResponse;
import com.example.bookingbadminton.payload.FieldRequest;
import com.example.bookingbadminton.payload.FieldDetailResponse;
import com.example.bookingbadminton.payload.FieldOwnerDetailResponse;
import com.example.bookingbadminton.payload.FieldOwnerDailyBookingResponse;
import com.example.bookingbadminton.payload.FieldOwnerSummaryResponse;
import com.example.bookingbadminton.payload.FieldOwnerBookingSummary;
import com.example.bookingbadminton.payload.FieldUserDetailResponse;
import com.example.bookingbadminton.payload.request.ValidOwnerRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FieldService {
    Field get(UUID id);

    Page<FieldCardResponse> search(String search, Pageable pageable);

    Page<FieldAdminResponse> adminList(String search, Pageable pageable);

    FieldDetailResponse detail(UUID id);

    Page<FieldOwnerSummaryResponse> ownerFields(UUID ownerId, Pageable pageable);

    FieldOwnerDetailResponse ownerFieldDetail(ValidOwnerRequest request, UUID fieldId);

    Field ownerUpdateField(UUID fieldId, FieldRequest request);

    Page<FieldOwnerBookingSummary> ownerFieldBookings(UUID ownerId, Pageable pageable);

    FieldOwnerDailyBookingResponse ownerDailyBookings(UUID fieldId, LocalDate date);

    FieldUserDetailResponse userDetail(UUID fieldId);
}
