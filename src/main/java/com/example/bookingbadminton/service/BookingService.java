package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.Enum.BookingStatus;
import com.example.bookingbadminton.model.entity.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface BookingService {
    List<Booking> findAll();

    Booking get(UUID id);

    Booking create(UUID fieldId, UUID userId, String msisdn, LocalDateTime startHour, LocalDateTime endHour, BookingStatus status);

    Booking update(UUID id, UUID fieldId, UUID userId, String msisdn, LocalDateTime startHour, LocalDateTime endHour, BookingStatus status);

    void delete(UUID id);
}
