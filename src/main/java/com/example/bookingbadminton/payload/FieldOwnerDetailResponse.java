package com.example.bookingbadminton.payload;

import com.example.bookingbadminton.model.Enum.ActiveStatus;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record FieldOwnerDetailResponse(
        UUID id,
        String name,
        String address,
        Integer quantity,
        String msisdn,
        String mobileContact,
        LocalTime startTime,
        LocalTime endTime,
        ActiveStatus active,
        String linkMap,
        List<String> images,
        List<TimeSlotResponse> timeSlots
) {
    public record TimeSlotResponse(UUID id, LocalTime startHour, LocalTime endHour, Integer price) {}
}
