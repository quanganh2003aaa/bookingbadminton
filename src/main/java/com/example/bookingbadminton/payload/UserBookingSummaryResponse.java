package com.example.bookingbadminton.payload;

import com.example.bookingbadminton.model.Enum.BookingStatus;

import java.time.LocalDate;
import java.util.UUID;

public record UserBookingSummaryResponse(
        UUID bookingId,
        String fieldName,
        LocalDate date,
        String timeRange,
        BookingStatus status
) {
}
