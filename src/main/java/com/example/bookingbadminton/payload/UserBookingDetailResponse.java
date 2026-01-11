package com.example.bookingbadminton.payload;

import com.example.bookingbadminton.model.Enum.BookingStatus;

import java.time.LocalDate;
import java.util.UUID;

public record UserBookingDetailResponse(
        UUID bookingId,
        String fieldName,
        String address,
        String mobileContact,
        LocalDate date,
        String timeRange,
        BookingStatus status
) {
}
