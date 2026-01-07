package com.example.bookingbadminton.payload;

import java.time.LocalTime;
import java.util.UUID;

public record FieldCardResponse(
        UUID id,
        String name,
        String address,
        LocalTime startTime,
        LocalTime endTime,
        String mobileContact,
        String avatar
) {
}
