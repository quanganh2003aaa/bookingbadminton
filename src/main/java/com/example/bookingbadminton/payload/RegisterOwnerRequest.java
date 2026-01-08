package com.example.bookingbadminton.payload;

import com.example.bookingbadminton.model.Enum.RegisterStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record RegisterOwnerRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank String address,
        @NotBlank @Size(max = 10) String mobileContact,
        @NotBlank @Size(max = 50) String gmail,
        @NotBlank @Size(max = 300) String password,
        @NotBlank @Size(min = 6) @Size(min = 6) String msiSdn,
        @NotNull String nameOwner,
        String linkMap
) {
}
