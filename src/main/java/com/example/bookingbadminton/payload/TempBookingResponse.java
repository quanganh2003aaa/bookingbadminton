package com.example.bookingbadminton.payload;

import com.example.bookingbadminton.model.Enum.BookingStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record TempBookingResponse(
        UUID parentFieldId,
        UUID userId,
        BookingStatus status,
        Integer totalAmount,
        List<TempBookingItem> bookings,
        String username,
        String msidn,
        String nameField,
        String addressField,
        String imgQr,
        UUID bookingId
) {
    public record TempBookingItem(
            UUID subFieldId,
            Integer indexField,
            LocalDate date,
            LocalTime startHour,
            LocalTime endHour,
            Integer price
    ) {}
}
