package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.FieldImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FieldImageRepository extends JpaRepository<FieldImage, UUID> {
    Optional<FieldImage> findFirstByField_Id(UUID fieldId);
    List<FieldImage> findByField_Id(UUID fieldId);
}
