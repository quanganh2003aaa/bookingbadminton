package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.RegisterOwner;
import com.example.bookingbadminton.model.Enum.RegisterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;
@Repository
public interface RegisterOwnerRepository extends JpaRepository<RegisterOwner, UUID> {
    @Query("""
            SELECT r FROM RegisterOwner r
            WHERE (:status IS NULL OR r.active = :status)
              AND (
                   :search IS NULL OR :search = '' OR
                   LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
                   LOWER(r.gmail) LIKE LOWER(CONCAT('%', :search, '%'))
              )
            ORDER BY r.createdAt DESC
            """)
    List<RegisterOwner> findByFilters(@Param("status") RegisterStatus status,
                                      @Param("search") String search);
}
