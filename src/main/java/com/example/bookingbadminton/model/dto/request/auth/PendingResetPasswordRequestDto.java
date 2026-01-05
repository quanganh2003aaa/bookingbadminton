package com.example.bookingbadminton.model.dto.request.auth;

import com.example.bookingbadminton.model.entity.Passcode;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PendingResetPasswordRequestDto {

    ForgotPasswordRequestDto request;
    Passcode passcode;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(passcode.getTime());
    }
}
