package com.example.bookingbadminton.model.dto.request.auth;

import com.example.bookingbadminton.model.entity.Passcode;
import com.example.bookingbadminton.payload.OwnerRequest;
import com.example.bookingbadminton.payload.RegisterOwnerRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PendingRegistrationRequestDto {
    RegisterOwnerRequest request;
    Passcode passcode;
    MultipartFile file;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(passcode.getTime());
    }
}
