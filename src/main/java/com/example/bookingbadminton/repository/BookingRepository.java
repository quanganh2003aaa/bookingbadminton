package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
}
