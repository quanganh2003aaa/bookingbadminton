package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, UUID> {
}
