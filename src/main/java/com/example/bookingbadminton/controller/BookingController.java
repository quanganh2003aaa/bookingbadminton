package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.model.Enum.BookingStatus;
import com.example.bookingbadminton.model.entity.Booking;
import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public ApiResponse list() {
        return ApiResponse.builder().result(bookingService.findAll()).build();
    }

    @GetMapping("/{id}")
    public ApiResponse get(@PathVariable UUID id) {
        return ApiResponse.builder()
                .result(bookingService.get(id))
                .build();
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody BookingRequest request) {
        Booking saved = bookingService.create(
                request.fieldId(),
                request.userId(),
                request.msisdn(),
                request.startHour(),
                request.endHour(),
                request.status()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder().result(saved).build());
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable UUID id, @RequestBody BookingRequest request) {
        return ApiResponse.builder()
                .result(bookingService.update(
                        id,
                        request.fieldId(),
                        request.userId(),
                        request.msisdn(),
                        request.startHour(),
                        request.endHour(),
                        request.status()
                ))
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        bookingService.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record BookingRequest(
            UUID fieldId,
            UUID userId,
            String msisdn,
            LocalDateTime startHour,
            LocalDateTime endHour,
            BookingStatus status
    ) {
    }
}
