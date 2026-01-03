package com.example.bookingbadminton.payload;

import com.example.bookingbadminton.model.Enum.BookingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingByDayResponse(
        UUID id,
        UUID fieldId,
        UUID userId,
        String msisdn,
        Integer indexField,
        LocalDateTime startHour,
        LocalDateTime endHour,
        BookingStatus status
) {
}
