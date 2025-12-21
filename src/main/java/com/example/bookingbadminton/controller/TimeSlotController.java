package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.model.entity.TimeSlot;
import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.payload.TimeSlotItemRequest;
import com.example.bookingbadminton.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/time-slots")
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    @GetMapping
    public ApiResponse list() {
        return ApiResponse.builder().result(timeSlotService.findAll()).build();
    }

    @GetMapping("/{id}")
    public ApiResponse get(@PathVariable UUID id) {
        return ApiResponse.builder()
                .result(timeSlotService.get(id))
                .build();
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody TimeSlotRequest request) {
        TimeSlot saved = timeSlotService.create(request.fieldId(), request.price(), request.startHour(), request.endHour());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder().result(saved).build());
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable UUID id, @RequestBody TimeSlotRequest request) {
        return ApiResponse.builder()
                .result(timeSlotService.update(id, request.fieldId(), request.price(), request.startHour(), request.endHour()))
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        timeSlotService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/field/{fieldId}")
    public ApiResponse listByField(@PathVariable UUID fieldId) {
        return ApiResponse.builder().result(timeSlotService.listByField(fieldId)).build();
    }

    @PostMapping("/field/{fieldId}")
    public ResponseEntity<ApiResponse> createForField(@PathVariable UUID fieldId, @RequestBody List<TimeSlotItemRequest> slots) {
        var saved = timeSlotService.setSlots(fieldId, slots);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder().result(saved).build());
    }

    public record TimeSlotRequest(
            UUID fieldId,
            Integer price,
            LocalTime startHour,
            LocalTime endHour
    ) {
    }
}
