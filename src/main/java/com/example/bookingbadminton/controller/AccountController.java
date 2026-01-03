package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.payload.request.LoginRequest;
import com.example.bookingbadminton.payload.request.RegisterOwnerRequest;
import com.example.bookingbadminton.payload.request.RegisterUserRequest;
import com.example.bookingbadminton.service.AccountService;
import com.example.bookingbadminton.service.OwnerService;
import com.example.bookingbadminton.service.UserService;
import jakarta.validation.Valid;
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
    private final UserService userService;
    private final OwnerService ownerService;

//    @GetMapping
//    public ApiResponse list() {
//        return ApiResponse.builder().result(accountService.findAll()).build();
//    }
//
//    @GetMapping("/{id}")
//    public ApiResponse get(@PathVariable UUID id) {
//        return ApiResponse.builder().result(accountService.get(id)).build();
//    }
//
//    @PostMapping
//    public ResponseEntity<ApiResponse> create(@RequestBody @Valid CreateAccountRequest request) {
//        Account saved = accountService.create(request);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(ApiResponse.builder().result(saved).build());
//    }
//
//    @PutMapping("/{id}")
//    public ApiResponse update(@PathVariable UUID id, @RequestBody Account request) {
//        return ApiResponse.builder().result(accountService.update(id, request)).build();
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable UUID id) {
//        accountService.delete(id);
//        return ResponseEntity.noContent().build();
//    }

    //API đăng nhập user
    @PostMapping("/login")
    public ApiResponse login(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.builder().result(accountService.login(request)).build();
    }

    //API đăng nhập owner
    @PostMapping("/login/owner")
    public ApiResponse loginOwner(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.builder().result(accountService.loginOwner(request)).build();
    }

    //API đăng nhập admin
    @PostMapping("/login/admin")
    public ApiResponse loginAdmin(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.builder().result(accountService.loginAdmin(request)).build();
    }

    //API khóa tài khoản người dùng
    @PostMapping("/{id}/lock")
    public ApiResponse lock(@PathVariable UUID id) {
        return ApiResponse.builder().result(accountService.lock(id)).build();
    }

    //API mở khóa tài khoản người dùng
    @PostMapping("/{id}/unlock")
    public ApiResponse unlock(@PathVariable UUID id) {
        return ApiResponse.builder().result(accountService.unlock(id)).build();
    }

    //API đăng ký tài khoản người dùng
    @PostMapping("/register/user")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody @Valid RegisterUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder().result( userService.create(request)).build());
    }

    //API đăng ký tài khoản owner
    @PostMapping("/register/owner")
    public ResponseEntity<ApiResponse> registerOwner(@RequestBody @Valid RegisterOwnerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder().result(ownerService.create(request)).build());
    }

}
