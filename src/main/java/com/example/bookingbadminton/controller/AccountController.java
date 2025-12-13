package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ApiResponse list() {
        return ApiResponse.builder().result(accountService.findAll()).build();
    }

    @GetMapping("/{id}")
    public ApiResponse get(@PathVariable UUID id) {
        return ApiResponse.builder().result(accountService.get(id)).build();
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody Account request) {
        Account saved = accountService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder().result(saved).build());
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable UUID id, @RequestBody Account request) {
        return ApiResponse.builder().result(accountService.update(id, request)).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
