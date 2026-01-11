package com.example.bookingbadminton.payload;

import com.example.bookingbadminton.model.Enum.ActiveStatus;

import java.util.UUID;

public record FieldOwnerSummaryResponse(
        UUID id,
        String name,
        String address,
        Integer quantity,
        Float averageRate,
        Long totalComments,
        ActiveStatus status
) {
}
