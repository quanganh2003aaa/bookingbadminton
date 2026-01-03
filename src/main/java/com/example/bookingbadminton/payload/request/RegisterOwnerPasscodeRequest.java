package com.example.bookingbadminton.payload.request;

import com.example.bookingbadminton.payload.CreateAccountRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record RegisterOwnerPasscodeRequest(
        @NotNull @Valid CreateAccountRequest account
) {
}
