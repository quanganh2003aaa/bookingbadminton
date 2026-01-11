package com.example.bookingbadminton.payload;

import com.example.bookingbadminton.model.Enum.BookingStatus;
import com.example.bookingbadminton.model.Enum.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PaidBookingDetailResponse(
        UUID bookingId,
        String user,
        String msisdn,
        BookingStatus status,
        InvoiceStatus invoiceStatus,
        LocalDateTime createdAt,
        BigDecimal price,
        String imgPayment,
        List<BookingSlot> bookingFields
) {
    public record BookingSlot(
            UUID subFieldId,
            Integer indexField,
            LocalDateTime startHour,
            LocalDateTime endHour
    ) {}
}
