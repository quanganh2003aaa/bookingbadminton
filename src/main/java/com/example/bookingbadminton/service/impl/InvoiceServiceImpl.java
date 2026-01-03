package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.Enum.InvoiceStatus;
import com.example.bookingbadminton.model.Enum.BookingStatus;
import com.example.bookingbadminton.model.entity.Booking;
import com.example.bookingbadminton.model.entity.Invoice;
import com.example.bookingbadminton.repository.BookingRepository;
import com.example.bookingbadminton.repository.InvoiceRepository;
import com.example.bookingbadminton.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    @Override
    public Invoice get(UUID id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
    }

    @Override
    public Invoice create(UUID bookingId, Integer price, InvoiceStatus status) {
        Invoice invoice = new Invoice();
        return saveInvoice(invoice, bookingId, price, status);
    }

    @Override
    public Invoice update(UUID id, UUID bookingId, Integer price, InvoiceStatus status) {
        Invoice invoice = get(id);
        return saveInvoice(invoice, bookingId, price, status);
    }

    private Invoice saveInvoice(Invoice invoice, UUID bookingId, Integer price, InvoiceStatus status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        invoice.setBooking(booking);
        invoice.setPrice(price);
        invoice.setStatus(status);
        if (status == InvoiceStatus.PAY) {
            booking.setStatus(BookingStatus.ACCEPT);
            bookingRepository.save(booking);
        }
        return invoiceRepository.save(invoice);
    }

    @Override
    public void delete(UUID id) {
        Invoice invoice = get(id);
        invoice.setDeletedAt(LocalDateTime.now());
        invoiceRepository.save(invoice);
    }
}
