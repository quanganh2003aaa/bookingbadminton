package com.example.bookingbadminton.payload;

import com.example.bookingbadminton.model.Enum.RegisterStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
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
