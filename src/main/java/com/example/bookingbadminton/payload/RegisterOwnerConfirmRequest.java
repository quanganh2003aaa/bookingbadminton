package com.example.bookingbadminton.payload;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record RegisterOwnerConfirmRequest(
        @NotNull UUID accountId,
        @NotBlank @Size(min = 6, max = 6) @Pattern(regexp = "\\d{6}") String code,
        @NotNull @Valid RegisterOwnerDraft register
) {
}
