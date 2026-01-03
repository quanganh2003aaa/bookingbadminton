package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    @Query("""
            SELECT b.field.id, COUNT(b) FROM Booking b
            WHERE b.startHour >= :startOfDay AND b.startHour < :endOfDay
            GROUP BY b.field.id
            """)
    List<Object[]> countByFieldInDay(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    List<Booking> findByStartHourBetween(LocalDateTime start, LocalDateTime end);

    List<Booking> findByField_IdAndStartHourBetween(UUID fieldId, LocalDateTime start, LocalDateTime end);
}
