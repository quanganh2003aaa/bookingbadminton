package com.example.bookingbadminton.payload;

import java.util.UUID;

public record FieldOwnerSummaryResponse(
        UUID id,
        String name,
        String address,
        Integer quantity,
        Float averageRate,
        Long totalComments
) {
}
