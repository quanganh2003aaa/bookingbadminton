package com.example.bookingbadminton.model.dto.request.keycloak;

import lombok.Data;

@Data
public class PasswordSyncRequest {
    private String keycloakUserId;
    private String newPassword;
}
