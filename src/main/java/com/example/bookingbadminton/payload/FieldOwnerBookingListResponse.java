package com.example.bookingbadminton.payload;

import com.example.bookingbadminton.model.Enum.BookingStatus;
import com.example.bookingbadminton.model.Enum.InvoiceStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record FieldOwnerBookingListResponse(
        UUID bookingId,
        UUID subFieldId,
        String user,
        String msisdn,
        BookingStatus status,
        LocalDateTime createdAt,
        InvoiceStatus invoiceStatus
) {

}
