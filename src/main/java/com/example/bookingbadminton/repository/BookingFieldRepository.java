package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.BookingField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface BookingFieldRepository extends JpaRepository<BookingField, Long> {

    @Query("""
            SELECT bf FROM BookingField bf
            JOIN bf.field f
            WHERE (f.id = :parentId OR f.parentField.id = :parentId)
              AND bf.startHour >= :startOfDay AND bf.startHour < :endOfDay
            """)
    List<BookingField> findByParentFieldAndDay(@Param("parentId") UUID parentId,
                                               @Param("startOfDay") LocalDateTime startOfDay,
                                               @Param("endOfDay") LocalDateTime endOfDay);
}
