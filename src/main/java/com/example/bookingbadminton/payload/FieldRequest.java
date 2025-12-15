package com.example.bookingbadminton.payload;

import com.example.bookingbadminton.model.Enum.ActiveStatus;

import java.time.LocalTime;
import java.util.UUID;

public record FieldRequest(
        UUID ownerId,
        String name,
        String address,
        Float ratePoint,
        String msisdn,
        String mobileContact,
        LocalTime startTime,
        LocalTime endTime,
        ActiveStatus active,
        String linkMap
) {
}
