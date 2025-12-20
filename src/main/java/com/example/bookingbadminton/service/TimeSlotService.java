package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.entity.TimeSlot;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface TimeSlotService {
    List<TimeSlot> findAll();

    TimeSlot get(UUID id);

    TimeSlot create(UUID fieldId, Integer price, LocalTime startHour, LocalTime endHour);

    TimeSlot update(UUID id, UUID fieldId, Integer price, LocalTime startHour, LocalTime endHour);

    void delete(UUID id);
}
