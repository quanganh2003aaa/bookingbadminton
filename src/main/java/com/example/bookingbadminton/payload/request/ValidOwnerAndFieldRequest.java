package com.example.bookingbadminton.payload.request;

import java.util.UUID;

public record ValidOwnerAndFieldRequest(
        UUID ownerId,
        UUID subFieldId
) {
}
