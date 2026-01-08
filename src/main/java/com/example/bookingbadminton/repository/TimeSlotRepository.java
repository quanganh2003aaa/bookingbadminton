package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, UUID> {
    List<TimeSlot> findByField_IdOrderByStartHour(UUID fieldId);
}
