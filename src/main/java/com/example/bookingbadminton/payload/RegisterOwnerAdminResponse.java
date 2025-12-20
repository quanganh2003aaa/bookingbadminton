package com.example.bookingbadminton.payload;

import com.example.bookingbadminton.model.Enum.RegisterStatus;
import java.util.UUID;

public record RegisterOwnerAdminResponse(
        UUID id,
        String name,
        String gmail,
        RegisterStatus status
) {
}
