package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.base.ResponseUtil;
import com.example.bookingbadminton.constant.SuccessMessage;
import com.example.bookingbadminton.model.dto.request.auth.ForgotPasswordRequestDto;
import com.example.bookingbadminton.model.dto.request.auth.LoginRequestDto;
import com.example.bookingbadminton.model.dto.request.auth.LogoutRequestDto;
import com.example.bookingbadminton.model.dto.request.auth.RefreshTokenRequestDto;
import com.example.bookingbadminton.model.dto.request.auth.ResetPasswordRequestDto;
import com.example.bookingbadminton.model.dto.request.auth.VerifyOtpRequestDto;
import com.example.bookingbadminton.model.dto.request.user.UserResponseDto;
import com.example.bookingbadminton.model.dto.response.ResponseData;
import com.example.bookingbadminton.model.dto.response.auth.AccountResponseDto;
import com.example.bookingbadminton.model.dto.response.auth.LoginResponseDto;
import com.example.bookingbadminton.model.dto.response.auth.RefreshTokenResponseDto;
import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.payload.RegisterOwnerRequest;
import com.example.bookingbadminton.payload.RegisterOwnerResponse;
import com.example.bookingbadminton.payload.request.RegisterUserRequest;
import com.example.bookingbadminton.service.AuthenticationService;
import com.example.bookingbadminton.util.OtpUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ResponseData<LoginResponseDto>> login(@RequestBody @Valid LoginRequestDto request) {
        return ResponseUtil.success(SuccessMessage.Auth.LOGIN_SUCCESS,
                authenticationService.authentication(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseData<Void>> logout(@RequestBody @Valid LogoutRequestDto request) {
        authenticationService.logout(request);
        return ResponseUtil.success(HttpStatus.OK, SuccessMessage.Auth.LOGOUT_SUCCESS);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseData<RefreshTokenResponseDto>> refresh(@RequestBody @Valid RefreshTokenRequestDto request) {
        return ResponseUtil.success(SuccessMessage.Auth.REFRESH_TOKEN_SUCCESS,
                authenticationService.refresh(request));
    }

    @PostMapping(value = "/register-owner", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseData<Void>> registerOwner(
            @RequestBody @Valid RegisterOwnerRequest request
    ) {
        authenticationService.registerOwner(request);
        return ResponseUtil.success(HttpStatus.OK, SuccessMessage.Auth.REGISTER_SEND_OTP_SUCCESS);
    }

    @PostMapping(value = "/verify-otp-register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseData<RegisterOwnerResponse>> verifyOtpToRegister(
            @RequestPart("request") @Valid VerifyOtpRequestDto request,
            @RequestPart("file") MultipartFile file
    ) {
        return ResponseUtil.success(SuccessMessage.Auth.VERIFY_OTP_REGISTER_SUCCESS,
                authenticationService.verifyOtpToRegister(request, file));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseData<Void>> forgotPassword(@RequestBody @Valid ForgotPasswordRequestDto request) {
        authenticationService.forgotPassword(request);
        return ResponseUtil.success(HttpStatus.OK, SuccessMessage.Auth.FORGOT_PASSWORD_SUCCESS);
    }

    @PostMapping("/verify-otp-reset-password")
    public ResponseEntity<ResponseData<Boolean>> verifyOtpToResetPassword(
            @RequestBody @Valid VerifyOtpRequestDto request
    ) {
        return ResponseUtil.success(SuccessMessage.Auth.VERIFY_OTP_TO_RESET_PASSWORD_SUCCESS,
                authenticationService.verifyOtpToResetPassword(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseData<AccountResponseDto>> resetPassword(@RequestBody @Valid ResetPasswordRequestDto request) {
        return ResponseUtil.success(SuccessMessage.Auth.RESET_PASSWORD_SUCCESS,
                authenticationService.resetPassword(request));
    }

    @PostMapping("/register-user")
    public ResponseEntity<ResponseData<UserResponseDto>> registerUser(@RequestBody @Valid RegisterUserRequest request) {
        return ResponseUtil.success("Thành công", authenticationService.registerUser(request));
    }

    @PostMapping("/accounts/{id}/lock")
    public ResponseEntity<ResponseData<Void>> lock(@PathVariable UUID id) {
        String message = authenticationService.lock(id);
        return ResponseUtil.success(HttpStatus.OK, message);
    }

    @PostMapping("/accounts/{id}/unlock")
    public ResponseEntity<ResponseData<Void>> unlock(@PathVariable UUID id) {
        String message = authenticationService.unlock(id);
        return ResponseUtil.success(HttpStatus.OK, message);
    }

    @GetMapping("/otp/generate")
    public ResponseEntity<ResponseData<OtpResponse>> generateOtp() {
        return ResponseUtil.success("Thành công", new OtpResponse(OtpUtil.generateOtp()));
    }

    public record OtpResponse(String otp) {}
}
