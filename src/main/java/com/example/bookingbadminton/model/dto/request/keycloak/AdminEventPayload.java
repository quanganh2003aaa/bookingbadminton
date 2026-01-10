package com.example.bookingbadminton.model.dto.request.keycloak;

import lombok.Data;

@Data
public class AdminEventPayload {
    private String operationType;
    private String resourceType;
    private String resourcePath;
    private String resourceId;
    private Object representation;
}
