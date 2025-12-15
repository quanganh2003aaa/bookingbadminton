package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FieldRepository extends JpaRepository<Field, UUID> {
    Page<Field> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
