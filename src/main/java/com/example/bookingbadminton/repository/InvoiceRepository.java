package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.Booking;
import com.example.bookingbadminton.model.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    Optional<Invoice> findByBooking(Booking booking);
}
