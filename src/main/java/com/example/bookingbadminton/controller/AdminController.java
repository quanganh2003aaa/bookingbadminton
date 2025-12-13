package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.model.entity.Admin;
import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public ApiResponse list() {
        return ApiResponse.builder().result(adminService.findAll()).build();
    }

    @GetMapping("/{id}")
    public ApiResponse get(@PathVariable UUID id) {
        return ApiResponse.builder().result(adminService.get(id)).build();
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody CreateAdminRequest request) {
        Admin saved = adminService.create(request.accountId(), request.name());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder().result(saved).build());
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable UUID id, @RequestBody CreateAdminRequest request) {
        return ApiResponse.builder()
                .result(adminService.update(id, request.accountId(), request.name()))
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        adminService.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record CreateAdminRequest(UUID accountId, String name) {
    }
}
