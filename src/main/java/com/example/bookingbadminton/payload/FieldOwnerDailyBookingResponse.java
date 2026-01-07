package com.example.bookingbadminton.payload;

import com.example.bookingbadminton.model.Enum.BookingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record FieldOwnerDailyBookingResponse(
        UUID fieldId,
        String fieldName,
        LocalDate date,
        List<SubFieldBooking> subFields
) {
    public record SubFieldBooking(
            UUID id,
            Integer indexField,
            List<BookingSlot> bookings
    ) {}

    public record BookingSlot(
            UUID bookingId,
            LocalDateTime startHour,
            LocalDateTime endHour,
            BookingStatus status
    ) {}
}
