package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.model.entity.RegisterOwner;
import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.payload.RegisterOwnerConfirmRequest;
import com.example.bookingbadminton.payload.RegisterOwnerRequest;
import com.example.bookingbadminton.payload.RegisterOwnerAdminResponse;
import com.example.bookingbadminton.model.Enum.RegisterStatus;
import com.example.bookingbadminton.payload.RegisterOwnerDetailResponse;
import com.example.bookingbadminton.payload.RegisterOwnerRejectResponse;
import com.example.bookingbadminton.service.RegisterOwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/register-owners")
@RequiredArgsConstructor
public class RegisterOwnerController {

    private final RegisterOwnerService registerOwnerService;

    @GetMapping
    public ApiResponse list() {
        return ApiResponse.builder().result(registerOwnerService.findAll()).build();
    }

    @GetMapping("/{id}")
    public ApiResponse get(@PathVariable UUID id) {
        return ApiResponse.builder()
                .result(registerOwnerService.get(id))
                .build();
    }

    @GetMapping("/{id}/detail")
    public ApiResponse detail(@PathVariable UUID id) {
        RegisterOwnerDetailResponse result = registerOwnerService.detail(id);
        return ApiResponse.builder().result(result).build();
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody @Valid RegisterOwnerRequest request) {
        RegisterOwner saved = registerOwnerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder().result(saved).build());
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable UUID id, @RequestBody @Valid RegisterOwnerRequest request) {
        return ApiResponse.builder().result(registerOwnerService.update(id, request)).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        registerOwnerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/confirm")
    public ApiResponse confirm(@RequestBody @Valid RegisterOwnerConfirmRequest request) {
        return ApiResponse.builder().result(registerOwnerService.confirm(request)).build();
    }

    @GetMapping("/admin")
    public ApiResponse adminList(@RequestParam(required = false) RegisterStatus status,
                                 @RequestParam(required = false) String search) {
        return ApiResponse.builder()
                .result(registerOwnerService.adminList(status, search))
                .build();
    }

    @PostMapping("/{id}/approve")
    public ApiResponse approve(@PathVariable UUID id) {
        return ApiResponse.builder().result(registerOwnerService.approve(id)).build();
    }

    @PostMapping("/{id}/reject")
    public ApiResponse reject(@PathVariable UUID id) {
        RegisterOwnerRejectResponse result = registerOwnerService.reject(id);
        return ApiResponse.builder().result(result).build();
    }
}
