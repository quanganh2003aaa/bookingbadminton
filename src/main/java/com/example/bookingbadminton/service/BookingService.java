package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.Enum.BookingStatus;
import com.example.bookingbadminton.model.entity.Booking;
import com.example.bookingbadminton.payload.BookingByDayResponse;
import com.example.bookingbadminton.payload.FieldOwnerDailyBookingResponse;
import com.example.bookingbadminton.payload.TempBookingRequest;
import com.example.bookingbadminton.payload.TempBookingResponse;
import com.example.bookingbadminton.payload.request.ValidOwnerAndFieldRequest;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingService {
    void approveBooking(UUID bookingId, ValidOwnerAndFieldRequest request);
    void rejectBooking(UUID bookingId, ValidOwnerAndFieldRequest request);
    Booking get(UUID id);

    FieldOwnerDailyBookingResponse bookingsByDayForField(UUID fieldId, LocalDate date);

    TempBookingResponse createTempPendingBooking(TempBookingRequest request);

//    List<BookingByDayResponse> findByDay(java.time.LocalDate date);
//
//    List<BookingByDayResponse> findByDayAndField(java.time.LocalDate date, UUID fieldId);
}
