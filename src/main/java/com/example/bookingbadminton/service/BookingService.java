package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.Enum.BookingStatus;
import com.example.bookingbadminton.model.entity.Booking;
import com.example.bookingbadminton.payload.BookingByDayResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface BookingService {
    List<Booking> findAll();

    Booking get(UUID id);

    Booking create(UUID fieldId, UUID userId, String msisdn, Integer indexField, LocalDateTime startHour, LocalDateTime endHour, BookingStatus status);

    Booking update(UUID id, UUID fieldId, UUID userId, String msisdn, Integer indexField, LocalDateTime startHour, LocalDateTime endHour, BookingStatus status);

    void delete(UUID id);

//    List<BookingByDayResponse> findByDay(java.time.LocalDate date);
//
//    List<BookingByDayResponse> findByDayAndField(java.time.LocalDate date, UUID fieldId);
}
