package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.Enum.ActiveStatus;
import com.example.bookingbadminton.model.Enum.TypePasscode;
import com.example.bookingbadminton.model.entity.Passcode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PasscodeRepository extends JpaRepository<Passcode, UUID> {
    Optional<Passcode> findByAccount_Id(UUID accountId);
    Optional<Passcode> findTopByAccount_IdAndTypeAndActiveOrderByTimeDesc(UUID accountId, TypePasscode type, ActiveStatus active);
}
