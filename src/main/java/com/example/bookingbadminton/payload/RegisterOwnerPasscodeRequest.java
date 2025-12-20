package com.example.bookingbadminton.payload;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record RegisterOwnerPasscodeRequest(
        @NotNull @Valid CreateAccountRequest account
) {
}
