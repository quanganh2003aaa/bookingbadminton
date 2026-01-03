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

    //API xác nhận tạo đơn đăng ký  thành công
    @PostMapping("/confirm")
    public ApiResponse confirm(@RequestBody @Valid RegisterOwnerConfirmRequest request) {
        return ApiResponse.builder().result(registerOwnerService.confirm(request)).build();
    }

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

}
