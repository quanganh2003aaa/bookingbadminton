package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.config.keycloak.KeycloakProperties;
import com.example.bookingbadminton.model.dto.request.keycloak.AdminEventPayload;
import com.example.bookingbadminton.model.dto.request.keycloak.PasswordSyncRequest;
import com.example.bookingbadminton.service.KeycloakAdminEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/keycloak/admin-event")
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdminEventController {

    private static final String TOKEN_HEADER = "X-KEYCLOAK-TOKEN";

    private final KeycloakAdminEventService keycloakAdminEventService;
    private final KeycloakProperties keycloakProperties;

    @PostMapping
    public ResponseEntity<Void> receive(@RequestBody AdminEventPayload payload,
                                        @RequestHeader(value = TOKEN_HEADER, required = false) String token) {
        if (!StringUtils.hasText(keycloakProperties.adminEventSecret())) {
            log.warn("Admin event secret not configured, skipping verification");
            return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).build();
        }
        if (!keycloakProperties.adminEventSecret().equals(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        keycloakAdminEventService.handle(payload);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password-sync")
    public ResponseEntity<Void> passwordSync(@RequestBody PasswordSyncRequest request,
                                             @RequestHeader(value = TOKEN_HEADER, required = false) String token) {
        if (!StringUtils.hasText(keycloakProperties.adminEventSecret())) {
            log.warn("Admin event secret not configured, skipping verification");
            return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).build();
        }
        if (!keycloakProperties.adminEventSecret().equals(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        keycloakAdminEventService.syncPassword(request.getKeycloakUserId(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
