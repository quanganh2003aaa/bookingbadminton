package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.Enum.ActiveStatus;
import com.example.bookingbadminton.model.Enum.TypePasscode;
import com.example.bookingbadminton.model.entity.Passcode;
import com.example.bookingbadminton.payload.request.RegisterOwnerPasscodeRequest;
import com.example.bookingbadminton.payload.RegisterOwnerPasscodeResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PasscodeService {
    List<Passcode> findAll();

    Passcode get(UUID id);

    Passcode create(UUID accountId, String code, TypePasscode type);

    Passcode update(UUID id, UUID accountId, String code, TypePasscode type, ActiveStatus active, LocalDateTime time, Integer totalDay, Integer totalMonth);

    void delete(UUID id);
}
