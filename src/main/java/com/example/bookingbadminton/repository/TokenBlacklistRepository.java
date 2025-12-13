package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, UUID> {
}
