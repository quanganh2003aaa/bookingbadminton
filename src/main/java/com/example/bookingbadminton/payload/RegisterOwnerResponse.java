package com.example.bookingbadminton.payload;

import com.example.bookingbadminton.model.Enum.RegisterStatus;

import java.util.UUID;

public record RegisterOwnerResponse(
        UUID id,
        UUID accountId,
        String name,
        String address,
        String mobileContact,
        String gmail,
        RegisterStatus active,
        String linkMap,
        String imgQr
) {
}
