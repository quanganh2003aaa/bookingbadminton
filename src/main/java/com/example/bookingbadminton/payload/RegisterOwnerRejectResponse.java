package com.example.bookingbadminton.payload;

import com.example.bookingbadminton.model.Enum.RegisterStatus;

import java.util.UUID;

public record RegisterOwnerRejectResponse(
        UUID id,
        RegisterStatus status
) {
}
