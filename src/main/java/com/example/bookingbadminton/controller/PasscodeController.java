package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.model.Enum.ActiveStatus;
import com.example.bookingbadminton.model.Enum.TypePasscode;
import com.example.bookingbadminton.model.entity.Passcode;
import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.service.PasscodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/passcodes")
@RequiredArgsConstructor
public class PasscodeController {

    private final PasscodeService passcodeService;

    @GetMapping
    public ApiResponse list() {
        return ApiResponse.builder().result(passcodeService.findAll()).build();
    }

    @GetMapping("/{id}")
    public ApiResponse get(@PathVariable UUID id) {
        return ApiResponse.builder()
                .result(passcodeService.get(id))
                .build();
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody PasscodeRequest request) {
        Passcode saved = passcodeService.create(
                request.accountId(),
                request.code(),
                request.type(),
                request.active(),
                request.time(),
                request.totalDay(),
                request.totalMonth()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder().result(saved).build());
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable UUID id, @RequestBody PasscodeRequest request) {
        return ApiResponse.builder()
                .result(passcodeService.update(
                        id,
                        request.accountId(),
                        request.code(),
                        request.type(),
                        request.active(),
                        request.time(),
                        request.totalDay(),
                        request.totalMonth()
                ))
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        passcodeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record PasscodeRequest(
            UUID accountId,
            String code,
            TypePasscode type,
            ActiveStatus active,
            LocalDateTime time,
            Integer totalDay,
            Integer totalMonth
    ) {
    }
}
