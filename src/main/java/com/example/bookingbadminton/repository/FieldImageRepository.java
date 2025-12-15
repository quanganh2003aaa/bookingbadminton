package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.FieldImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FieldImageRepository extends JpaRepository<FieldImage, UUID> {
    Optional<FieldImage> findFirstByField_Id(UUID fieldId);
}
