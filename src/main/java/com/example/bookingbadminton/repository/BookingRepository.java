package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.Booking;
import com.example.bookingbadminton.model.entity.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    Optional<Booking> findById(UUID id);
    @Query("""
            SELECT COALESCE(f.parentField.id, f.id) AS parentId, COUNT(bf) FROM BookingField bf
            JOIN bf.field f
            WHERE bf.startHour >= :startOfDay AND bf.startHour < :endOfDay
            GROUP BY COALESCE(f.parentField.id, f.id)
            """)
    List<Object[]> countByParentFieldInDay(@Param("startOfDay") LocalDateTime startOfDay,
                                           @Param("endOfDay") LocalDateTime endOfDay);

    List<Booking> findByUser_IdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID userId);
}
