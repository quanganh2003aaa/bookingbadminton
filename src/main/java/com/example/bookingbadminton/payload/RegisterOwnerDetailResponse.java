package com.example.bookingbadminton.payload;

import com.example.bookingbadminton.model.Enum.RegisterStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegisterOwnerDetailResponse(
        UUID id,
        UUID accountId,
        String name,
        String address,
        String mobileContact,
        String gmail,
        RegisterStatus status,
        String linkMap,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
}
