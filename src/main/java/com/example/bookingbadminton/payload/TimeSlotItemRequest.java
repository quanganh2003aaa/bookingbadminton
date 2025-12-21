package com.example.bookingbadminton.payload;

import java.time.LocalTime;

public record TimeSlotItemRequest(
        Integer price,
        LocalTime startHour,
        LocalTime endHour
) {
}
