package com.example.bookingbadminton.model.dto.response.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class LoginResponseDto {

    String tokenType;

    String keycloakUserId;

    String userId;

    String role;

    String accessToken;

    String refreshToken;
}
