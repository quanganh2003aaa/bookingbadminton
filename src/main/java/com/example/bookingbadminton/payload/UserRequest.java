package com.example.bookingbadminton.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserRequest(
        @NotNull UUID accountId,
        @NotBlank @Size(max = 50) String name,
        String avatar
) {
}
