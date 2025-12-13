package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.Enum.InvoiceStatus;
import com.example.bookingbadminton.model.entity.Invoice;

import java.util.List;
import java.util.UUID;

public interface InvoiceService {
    List<Invoice> findAll();

    Invoice get(UUID id);

    Invoice create(UUID bookingId, Integer price, InvoiceStatus status);

    Invoice update(UUID id, UUID bookingId, Integer price, InvoiceStatus status);

    void delete(UUID id);
}
