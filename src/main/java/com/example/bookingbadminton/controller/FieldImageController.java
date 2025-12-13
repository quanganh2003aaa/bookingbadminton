package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.model.Enum.TypeImage;
import com.example.bookingbadminton.model.entity.FieldImage;
import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.service.FieldImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/field-images")
@RequiredArgsConstructor
public class FieldImageController {

    private final FieldImageService fieldImageService;

    @GetMapping
    public ApiResponse list() {
        return ApiResponse.builder().result(fieldImageService.findAll()).build();
    }

    @GetMapping("/{id}")
    public ApiResponse get(@PathVariable UUID id) {
        return ApiResponse.builder()
                .result(fieldImageService.get(id))
                .build();
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody FieldImageRequest request) {
        FieldImage saved = fieldImageService.create(request.fieldId(), request.type(), request.image());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder().result(saved).build());
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable UUID id, @RequestBody FieldImageRequest request) {
        return ApiResponse.builder()
                .result(fieldImageService.update(id, request.fieldId(), request.type(), request.image()))
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        fieldImageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record FieldImageRequest(UUID fieldId, TypeImage type, String image) {
    }
}
