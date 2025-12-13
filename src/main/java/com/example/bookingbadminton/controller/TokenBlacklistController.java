package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.model.entity.TokenBlacklist;
import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/token-blacklist")
@RequiredArgsConstructor
public class TokenBlacklistController {

    private final TokenBlacklistService tokenBlacklistService;

    @GetMapping
    public ApiResponse list() {
        return ApiResponse.builder().result(tokenBlacklistService.findAll()).build();
    }

    @GetMapping("/{id}")
    public ApiResponse get(@PathVariable UUID id) {
        return ApiResponse.builder()
                .result(tokenBlacklistService.get(id))
                .build();
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody TokenBlacklistRequest request) {
        TokenBlacklist saved = tokenBlacklistService.create(request.accountId(), request.jit(), request.expiredAt());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder().result(saved).build());
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable UUID id, @RequestBody TokenBlacklistRequest request) {
        return ApiResponse.builder()
                .result(tokenBlacklistService.update(id, request.accountId(), request.jit(), request.expiredAt()))
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        tokenBlacklistService.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record TokenBlacklistRequest(
            UUID accountId,
            UUID jit,
            LocalDateTime expiredAt
    ) {
    }
}
