package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    boolean existsByGmailIgnoreCase(String gmail);
    Optional<Account> findByGmailIgnoreCase(String gmail);
    Optional<Account> findByKeycloakUserId(String keycloakUserId);
}
