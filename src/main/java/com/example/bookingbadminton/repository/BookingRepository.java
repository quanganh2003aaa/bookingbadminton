package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.Booking;
import com.example.bookingbadminton.model.entity.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    Optional<Booking> findByIdAndField(UUID id, Field field);
    @Query("""
            SELECT bf.field.id, COUNT(bf) FROM BookingField bf
            WHERE bf.startHour >= :startOfDay AND bf.startHour < :endOfDay
            GROUP BY bf.field.id
            """)
    List<Object[]> countByFieldInDay(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("""
            SELECT COALESCE(f.parentField.id, f.id) AS parentId, COUNT(bf) FROM BookingField bf
            JOIN bf.field f
            WHERE bf.startHour >= :startOfDay AND bf.startHour < :endOfDay
            GROUP BY COALESCE(f.parentField.id, f.id)
            """)
    List<Object[]> countByParentFieldInDay(@Param("startOfDay") LocalDateTime startOfDay,
                                           @Param("endOfDay") LocalDateTime endOfDay);

    @Query("""
            SELECT DISTINCT bf.booking FROM BookingField bf
            WHERE bf.startHour >= :start AND bf.startHour < :end
            """)
    List<Booking> findByStartHourBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
            SELECT DISTINCT bf.booking FROM BookingField bf
            WHERE bf.field.id = :fieldId
              AND bf.startHour >= :start AND bf.startHour < :end
            """)
    List<Booking> findByField_IdAndStartHourBetween(@Param("fieldId") UUID fieldId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
