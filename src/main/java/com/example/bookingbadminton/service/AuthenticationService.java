package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.dto.request.auth.*;
import com.example.bookingbadminton.model.dto.request.user.UserResponseDto;
import com.example.bookingbadminton.model.dto.response.auth.AccountResponseDto;
import com.example.bookingbadminton.model.dto.response.auth.LoginResponseDto;
import com.example.bookingbadminton.model.dto.response.auth.RefreshTokenResponseDto;
import com.example.bookingbadminton.payload.RegisterOwnerRequest;
import com.example.bookingbadminton.payload.RegisterOwnerResponse;
import com.example.bookingbadminton.payload.request.RegisterUserRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface AuthenticationService {
    LoginResponseDto authentication(LoginRequestDto request);

    String lock(UUID id);

    String unlock(UUID id);

    void registerOwner(RegisterOwnerRequest request);

    RegisterOwnerResponse verifyOtpToRegister(VerifyOtpRequestDto request, MultipartFile file);

    void logout (LogoutRequestDto logoutRequestDto);

    void forgotPassword(ForgotPasswordRequestDto request);

    boolean verifyOtpToResetPassword(VerifyOtpRequestDto request);

    AccountResponseDto resetPassword(ResetPasswordRequestDto request);

    RefreshTokenResponseDto refresh(RefreshTokenRequestDto request);

    UserResponseDto registerUser(RegisterUserRequest request);
}
