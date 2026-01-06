package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.service.EmailService;
import com.example.bookingbadminton.util.OtpUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    /**
     * Test sending registration OTP email via SendGrid.
     */
    @PostMapping("/registration-otp")
    public ApiResponse sendRegistrationOtp(@RequestBody @Valid SendEmailRequest request) {
        String otp = (request.otp() == null || request.otp().isBlank())
                ? OtpUtil.generateOtp()
                : request.otp();

        emailService.sendRegistrationOtpByEmail(request.to(), request.name(), otp);

        return ApiResponse.builder()
                .message("Registration OTP email triggered")
                .result(Map.of("to", request.to(), "otp", otp))
                .build();
    }

    /**
     * Test sending forgot-password OTP email via SendGrid.
     */
    @PostMapping("/forgot-password-otp")
    public ApiResponse sendForgotPasswordOtp(@RequestBody @Valid SendEmailRequest request) {
        String otp = (request.otp() == null || request.otp().isBlank())
                ? OtpUtil.generateOtp()
                : request.otp();

        emailService.sendForgotPasswordOtpByEmail(request.to(), request.name(), otp);

        return ApiResponse.builder()
                .message("Forgot password OTP email triggered")
                .result(Map.of("to", request.to(), "otp", otp))
                .build();
    }

    public record SendEmailRequest(
            @NotBlank @Email String to,
            @NotBlank String name,
            String otp
    ) {}
}
