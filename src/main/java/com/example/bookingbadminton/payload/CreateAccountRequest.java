package com.example.bookingbadminton.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(
        @NotBlank @Size(min = 6, max = 300) String password,
        @NotBlank @Email @Size(max = 50) String gmail,
        @NotBlank @Size(min = 9, max = 10) String msisdn
) {
}
