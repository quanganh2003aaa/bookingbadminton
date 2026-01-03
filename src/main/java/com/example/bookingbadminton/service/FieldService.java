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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FieldService {
    List<Field> findAll();

    Field get(UUID id);

    Field create(FieldRequest request);

    Field update(UUID id, FieldRequest request);

    void delete(UUID id);

    Page<FieldCardResponse> search(String search, com.example.bookingbadminton.model.Enum.ActiveStatus active, Pageable pageable);

    Page<FieldAdminResponse> adminList(String search, Pageable pageable);

    FieldDetailResponse detail(UUID id);

    Page<FieldOwnerSummaryResponse> ownerFields(UUID ownerId, Pageable pageable);

    FieldOwnerDetailResponse ownerFieldDetail(UUID ownerId, UUID fieldId);

    Field ownerUpdate(UUID ownerId, UUID fieldId, FieldRequest request);

    Page<FieldOwnerBookingSummary> ownerFieldBookings(UUID ownerId, Pageable pageable);

    FieldOwnerDailyBookingResponse ownerDailyBookings(UUID ownerId, UUID fieldId, LocalDate date);

    FieldUserDetailResponse userDetail(UUID fieldId);
}
