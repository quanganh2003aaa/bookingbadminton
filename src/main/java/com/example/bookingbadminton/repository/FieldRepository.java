package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.Field;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FieldRepository extends JpaRepository<Field, UUID> {
}
