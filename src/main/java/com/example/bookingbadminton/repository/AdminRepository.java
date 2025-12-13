package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdminRepository extends JpaRepository<Admin, UUID> {
}
