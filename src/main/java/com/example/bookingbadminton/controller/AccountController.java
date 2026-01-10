package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.base.ResponseUtil;
import com.example.bookingbadminton.constant.SuccessMessage;
import com.example.bookingbadminton.model.dto.request.auth.LoginRequestDto;
import com.example.bookingbadminton.model.dto.response.ResponseData;
import com.example.bookingbadminton.model.dto.response.auth.LoginResponseDto;
import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.payload.request.LoginRequest;
import com.example.bookingbadminton.payload.request.RegisterOwnerRequest;
import com.example.bookingbadminton.payload.request.RegisterUserRequest;
import com.example.bookingbadminton.service.AuthenticationService;
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

     private final UserService userService;
    private final OwnerService ownerService;
    private final AuthenticationService authenticationService;

    //API đăng nhập user
    @PostMapping("/login")
    public ResponseEntity<ResponseData<LoginResponseDto>> login(@RequestBody @Valid LoginRequestDto request) {
        return ResponseUtil.success(SuccessMessage.Auth.LOGIN_SUCCESS,
                authenticationService.authentication(request)
        );
    }

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

//    //API đăng ký tài khoản người dùng
//    @PostMapping("/register-user")
//    public ResponseEntity<ApiResponse> registerUser(@RequestBody @Valid RegisterUserRequest request) {
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(ApiResponse.builder().result( userService.create(request)).build());
//    }

}
