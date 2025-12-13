package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.model.Enum.RegisterStatus;
import com.example.bookingbadminton.model.entity.RegisterOwner;
import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.service.RegisterOwnerService;
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

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody RegisterOwnerRequest request) {
        RegisterOwner saved = registerOwnerService.create(
                request.accountId(),
                request.name(),
                request.address(),
                request.mobileContact(),
                request.gmail(),
                request.active(),
                request.linkMap()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder().result(saved).build());
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable UUID id, @RequestBody RegisterOwnerRequest request) {
        return ApiResponse.builder().result(registerOwnerService.update(
                id,
                request.accountId(),
                request.name(),
                request.address(),
                request.mobileContact(),
                request.gmail(),
                request.active(),
                request.linkMap()
        )).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        registerOwnerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record RegisterOwnerRequest(
            UUID accountId,
            String name,
            String address,
            String mobileContact,
            String gmail,
            RegisterStatus active,
            String linkMap
    ) {
    }
}
