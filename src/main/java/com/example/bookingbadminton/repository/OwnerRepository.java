package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OwnerRepository extends JpaRepository<Owner, UUID> {
    Optional<Owner> findByAccount_Id(UUID accountId);
}
