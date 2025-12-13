package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.entity.Field;
import com.example.bookingbadminton.model.entity.TimeSlot;
import com.example.bookingbadminton.repository.FieldRepository;
import com.example.bookingbadminton.repository.TimeSlotRepository;
import com.example.bookingbadminton.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TimeSlotServiceImpl implements TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final FieldRepository fieldRepository;

    @Override
    public List<TimeSlot> findAll() {
        return timeSlotRepository.findAll();
    }

    @Override
    public TimeSlot get(UUID id) {
        return timeSlotRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Time slot not found"));
    }

    @Override
    public TimeSlot create(UUID fieldId, Integer price, LocalDateTime startHour, LocalDateTime endHour) {
        TimeSlot slot = new TimeSlot();
        return saveSlot(slot, fieldId, price, startHour, endHour);
    }

    @Override
    public TimeSlot update(UUID id, UUID fieldId, Integer price, LocalDateTime startHour, LocalDateTime endHour) {
        TimeSlot slot = get(id);
        return saveSlot(slot, fieldId, price, startHour, endHour);
    }

    private TimeSlot saveSlot(TimeSlot slot, UUID fieldId, Integer price, LocalDateTime startHour, LocalDateTime endHour) {
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Field not found"));
        slot.setField(field);
        slot.setPrice(price);
        slot.setStartHour(startHour);
        slot.setEndHour(endHour);
        return timeSlotRepository.save(slot);
    }

    @Override
    public void delete(UUID id) {
        if (!timeSlotRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Time slot not found");
        }
        timeSlotRepository.deleteById(id);
    }
}
