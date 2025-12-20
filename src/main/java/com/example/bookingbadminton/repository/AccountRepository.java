package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    boolean existsByGmailIgnoreCase(String gmail);
    boolean existsByMsisdn(String msisdn);
    Optional<Account> findByGmailIgnoreCase(String gmail);
}
