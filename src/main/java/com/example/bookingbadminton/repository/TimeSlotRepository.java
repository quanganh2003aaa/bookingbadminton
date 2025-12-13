package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, UUID> {
}
