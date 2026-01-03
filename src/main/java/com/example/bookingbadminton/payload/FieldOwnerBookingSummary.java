package com.example.bookingbadminton.payload;

import com.example.bookingbadminton.model.Enum.ActiveStatus;

import java.util.UUID;

public record FieldOwnerBookingSummary(
        UUID id,
        String name,
        long todayBookings,
        ActiveStatus status
) {
}
