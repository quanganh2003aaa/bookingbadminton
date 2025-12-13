package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.FieldImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FieldImageRepository extends JpaRepository<FieldImage, UUID> {
}
