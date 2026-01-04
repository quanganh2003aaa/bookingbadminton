package com.example.bookingbadminton.payload;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record TempBookingRequest(
        @NotNull UUID parentFieldId,
        @NotNull UUID userId,
        @NotNull List<TempBookingItem> listBookingField
) {
    public record TempBookingItem(
            @NotNull UUID subFieldId,
            @NotNull LocalTime startHour,
            @NotNull LocalTime endHour,
            @NotNull LocalDate date
    ) {}
}
