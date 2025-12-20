package com.example.bookingbadminton.payload;

import com.example.bookingbadminton.model.Enum.ActiveStatus;

import java.time.LocalTime;
import java.util.UUID;

public record FieldDetailResponse(
        UUID id,
        String name,
        String address,
        String ownerName,
        String ownerGmail,
        String msisdn,
        String mobileContact,
        LocalTime startTime,
        LocalTime endTime,
        ActiveStatus active,
        String linkMap
) {
}
