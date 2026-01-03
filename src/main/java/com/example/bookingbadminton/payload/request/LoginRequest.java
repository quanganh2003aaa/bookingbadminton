package com.example.bookingbadminton.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank @Email @Size(max = 50) String gmail,
        @NotBlank @Size(min = 6, max = 300) String password
) {
}
