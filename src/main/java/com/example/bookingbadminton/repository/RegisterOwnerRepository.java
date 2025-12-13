package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.RegisterOwner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RegisterOwnerRepository extends JpaRepository<RegisterOwner, UUID> {
}
