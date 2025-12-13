package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.Enum.ActiveStatus;
import com.example.bookingbadminton.model.entity.Field;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface FieldService {
    List<Field> findAll();

    Field get(UUID id);

    Field create(UUID accountId, String name, String address, Float ratePoint, String msisdn,
                 String mobileContact, LocalTime startTime, LocalTime endTime, ActiveStatus active, String linkMap);

    Field update(UUID id, UUID accountId, String name, String address, Float ratePoint, String msisdn,
                 String mobileContact, LocalTime startTime, LocalTime endTime, ActiveStatus active, String linkMap);

    void delete(UUID id);
}
