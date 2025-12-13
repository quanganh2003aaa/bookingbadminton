package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.model.entity.RefreshToken;
import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/refresh-tokens")
@RequiredArgsConstructor
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    @GetMapping
    public ApiResponse list() {
        return ApiResponse.builder().result(refreshTokenService.findAll()).build();
    }

    @GetMapping("/{id}")
    public ApiResponse get(@PathVariable UUID id) {
        return ApiResponse.builder()
                .result(refreshTokenService.get(id))
                .build();
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody RefreshTokenRequest request) {
        RefreshToken saved = refreshTokenService.create(
                request.accountId(),
                request.tokenHash(),
                request.expiredAt(),
                request.revoked(),
                request.revokedAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder().result(saved).build());
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable UUID id, @RequestBody RefreshTokenRequest request) {
        return ApiResponse.builder().result(refreshTokenService.update(
                id,
                request.accountId(),
                request.tokenHash(),
                request.expiredAt(),
                request.revoked(),
                request.revokedAt()
        )).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        refreshTokenService.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record RefreshTokenRequest(
            UUID accountId,
            String tokenHash,
            LocalDateTime expiredAt,
            Boolean revoked,
            LocalDateTime revokedAt
    ) {
    }
}
