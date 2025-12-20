package com.example.bookingbadminton.payload;

import java.util.UUID;

public record FieldAdminResponse(
        UUID id,
        String name,
        String gmail
) {
}
