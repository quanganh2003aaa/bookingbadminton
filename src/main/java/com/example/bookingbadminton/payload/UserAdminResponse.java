package com.example.bookingbadminton.payload;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserAdminResponse(
        UUID id,
        String msisdn,
        String gmail,
        String name,
        LocalDateTime deletedAt
) {
}
