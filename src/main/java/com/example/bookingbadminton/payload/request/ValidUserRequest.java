package com.example.bookingbadminton.payload.request;

import java.util.UUID;

public record ValidUserRequest(
        UUID userId
) {
}
