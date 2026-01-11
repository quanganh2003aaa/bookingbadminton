package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.Enum.ActiveStatus;
import com.example.bookingbadminton.model.Enum.TypePasscode;
import com.example.bookingbadminton.model.entity.Passcode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface PasscodeRepository extends JpaRepository<Passcode, UUID> {
    Optional<Passcode> findByAccount_Id(UUID accountId);
}
