package com.example.bookingbadminton.model.Enum;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PaymentStatus {
    @JsonProperty("pending")
    PENDING,

    @JsonProperty("completed")
    COMPLETED,

    @JsonProperty("expired")
    EXPIRED,

    @JsonProperty("cancelled")
    CANCELLED,

    @JsonProperty("refunded")
    REFUNDED
}
