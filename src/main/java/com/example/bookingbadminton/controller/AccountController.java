package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AuthenticationService authenticationService;

    //API khóa tài khoản người dùng
    @PostMapping("/{id}/lock")
    public ApiResponse lock(@PathVariable UUID id) {
        return ApiResponse.builder().result(authenticationService.lock(id)).build();
    }

    //API mở khóa tài khoản người dùng
    @PostMapping("/{id}/unlock")
    public ApiResponse unlock(@PathVariable UUID id) {
        return ApiResponse.builder().result(authenticationService.unlock(id)).build();
    }

}
