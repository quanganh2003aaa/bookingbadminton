package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.model.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface OwnerRepository extends JpaRepository<Owner, UUID> {
    Optional<Owner> findByAccount_Id(UUID accountId);

    Optional<Owner> findByAccount(Account account);
}
