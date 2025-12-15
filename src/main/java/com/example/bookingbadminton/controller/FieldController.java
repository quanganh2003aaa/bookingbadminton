package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.model.Enum.ActiveStatus;
import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.payload.FieldCardResponse;
import com.example.bookingbadminton.payload.FieldRequest;
import com.example.bookingbadminton.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/fields")
@RequiredArgsConstructor
public class FieldController {

    private final FieldService fieldService;

    @GetMapping
    public ApiResponse list(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(required = false) String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FieldCardResponse> result = fieldService.search(search, pageable);
        return ApiResponse.builder().result(result).build();
    }

    @GetMapping("/{id}")
    public ApiResponse get(@PathVariable UUID id) {
        return ApiResponse.builder().result(fieldService.get(id)).build();
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody FieldRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .result(fieldService.create(r))
                .build());
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable UUID id, @RequestBody FieldRequest request) {
        return ApiResponse.builder().result(fieldService.update(id, request)).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        fieldService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
