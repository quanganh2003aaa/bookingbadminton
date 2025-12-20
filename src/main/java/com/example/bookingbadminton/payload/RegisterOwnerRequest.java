package com.example.bookingbadminton.payload;

import com.example.bookingbadminton.model.Enum.RegisterStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record RegisterOwnerRequest(
        @NotNull UUID accountId,
        @NotBlank @Size(max = 255) String name,
        @NotBlank String address,
        @NotBlank @Size(max = 10) String mobileContact,
        @NotBlank @Size(max = 50) String gmail,
        @NotNull RegisterStatus active,
        String linkMap
) {
}
