package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.controller.TimeSlotController;
import com.example.bookingbadminton.model.dto.TimeSlotDTO;
import com.example.bookingbadminton.model.entity.Field;
import com.example.bookingbadminton.model.entity.TimeSlot;
import com.example.bookingbadminton.payload.TimeSlotItemRequest;
import com.example.bookingbadminton.repository.FieldRepository;
import com.example.bookingbadminton.repository.TimeSlotRepository;
import com.example.bookingbadminton.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Comparator;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TimeSlotServiceImpl implements TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final FieldRepository fieldRepository;
    @Override
    public List<TimeSlotDTO> listByField(UUID fieldId) {
        return timeSlotRepository.findByField_IdOrderByStartHour(fieldId).stream().map(TimeSlotDTO::new).toList();
    }

    @Override
    public List<TimeSlotDTO> setSlots(UUID fieldId, List<TimeSlotItemRequest> slots) {
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sân"));
        if (field.getStartTime() == null || field.getEndTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chưa thiết lập giờ hoạt động của sân");
        }
        if (slots == null || slots.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Danh sách khung giờ trống");
        }
        slots = slots.stream()
                .sorted(Comparator.comparing(TimeSlotItemRequest::startHour))
                .toList();
        LocalTime expectedStart = field.getStartTime();
        for (TimeSlotItemRequest s : slots) {
            if (s.startHour() == null || s.endHour() == null || !s.startHour().isBefore(s.endHour())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khoảng thời gian không hợp lệ");
            }
            if (!s.startHour().equals(expectedStart)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khung giờ phải liên tục từ giờ mở cửa");
            }
            expectedStart = s.endHour();
            if (expectedStart.isAfter(field.getEndTime())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khung giờ vượt quá giờ đóng cửa");
            }
        }
        if (!expectedStart.equals(field.getEndTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khung giờ phải kết thúc đúng giờ đóng cửa");
        }

        List<TimeSlot> existing = timeSlotRepository.findByField_IdOrderByStartHour(fieldId);
        timeSlotRepository.deleteAll(existing);

        return slots.stream().map(s -> {
            TimeSlot slot = new TimeSlot();
            slot.setField(field);
            slot.setPrice(s.price());
            slot.setStartHour(s.startHour());
            slot.setEndHour(s.endHour());
            timeSlotRepository.save(slot);
            return new TimeSlotDTO(slot);
        }).toList();
    }
}
