package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.model.Enum.ActiveStatus;
import com.example.bookingbadminton.model.entity.Field;
import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/fields")
@RequiredArgsConstructor
public class FieldController {

    private final FieldService fieldService;

    @GetMapping
    public ApiResponse list() {
        return ApiResponse.builder().result(fieldService.findAll()).build();
    }

    @GetMapping("/{id}")
    public ApiResponse get(@PathVariable UUID id) {
        return ApiResponse.builder().result(fieldService.get(id)).build();
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody FieldRequest request) {
        Field saved = fieldService.create(
                request.accountId(),
                request.name(),
                request.address(),
                request.ratePoint(),
                request.msisdn(),
                request.mobileContact(),
                request.startTime(),
                request.endTime(),
                request.active(),
                request.linkMap()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder().result(saved).build());
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable UUID id, @RequestBody FieldRequest request) {
        return ApiResponse.builder().result(fieldService.update(
                id,
                request.accountId(),
                request.name(),
                request.address(),
                request.ratePoint(),
                request.msisdn(),
                request.mobileContact(),
                request.startTime(),
                request.endTime(),
                request.active(),
                request.linkMap()
        )).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        fieldService.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record FieldRequest(
            UUID accountId,
            String name,
            String address,
            Float ratePoint,
            String msisdn,
            String mobileContact,
            LocalTime startTime,
            LocalTime endTime,
            ActiveStatus active,
            String linkMap
    ) {
    }
}
