package com.example.bookingbadminton.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterOwnerDraft(
        @NotBlank @Size(max = 255) String name,
        @NotBlank String address,
        @NotBlank @Size(max = 10) String mobileContact,
        String linkMap,
        String imgQr
) {
}
