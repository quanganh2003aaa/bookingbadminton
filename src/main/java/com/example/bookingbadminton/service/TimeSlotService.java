package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.dto.TimeSlotDTO;
import com.example.bookingbadminton.model.entity.TimeSlot;
import com.example.bookingbadminton.payload.TimeSlotItemRequest;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface TimeSlotService {

    List<TimeSlotDTO> listByField(UUID fieldId);

    List<TimeSlotDTO> setSlots(UUID fieldId, List<TimeSlotItemRequest> slots);
}
