package com.example.bookingbadminton.payload;

import com.example.bookingbadminton.model.Enum.ActiveStatus;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record FieldUserDetailResponse(
        UUID id,
        String name,
        String address,
        String mobileContact,
        String avatar,
        LocalTime startTime,
        LocalTime endTime,
        ActiveStatus active,
        String linkMap,
        List<FieldCommentResponse> comments,
        List<TimeSlotResponse> timeSlots
) {
    public record FieldCommentResponse(String userName, Float rate, String content) {}
    public record TimeSlotResponse(UUID id, LocalTime startHour, LocalTime endHour, Integer price) {}
}
