package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.Enum.ActiveStatus;
import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.model.entity.Field;
import com.example.bookingbadminton.repository.AccountRepository;
import com.example.bookingbadminton.repository.FieldRepository;
import com.example.bookingbadminton.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FieldServiceImpl implements FieldService {

    private final FieldRepository fieldRepository;
    private final AccountRepository accountRepository;

    @Override
    public List<Field> findAll() {
        return fieldRepository.findAll();
    }

    @Override
    public Field get(UUID id) {
        return fieldRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Field not found"));
    }

    @Override
    public Field create(UUID accountId, String name, String address, Float ratePoint, String msisdn, String mobileContact, LocalTime startTime, LocalTime endTime, ActiveStatus active, String linkMap) {
        Field field = new Field();
        return saveField(field, accountId, name, address, ratePoint, msisdn, mobileContact, startTime, endTime, active, linkMap);
    }

    @Override
    public Field update(UUID id, UUID accountId, String name, String address, Float ratePoint, String msisdn, String mobileContact, LocalTime startTime, LocalTime endTime, ActiveStatus active, String linkMap) {
        Field field = get(id);
        return saveField(field, accountId, name, address, ratePoint, msisdn, mobileContact, startTime, endTime, active, linkMap);
    }

    private Field saveField(Field field, UUID accountId, String name, String address, Float ratePoint, String msisdn, String mobileContact, LocalTime startTime, LocalTime endTime, ActiveStatus active, String linkMap) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        field.setAccount(account);
        field.setName(name);
        field.setAddress(address);
        field.setRatePoint(ratePoint);
        field.setMsisdn(msisdn);
        field.setMobileContact(mobileContact);
        field.setStartTime(startTime);
        field.setEndTime(endTime);
        field.setActive(active);
        field.setLinkMap(linkMap);
        return fieldRepository.save(field);
    }

    @Override
    public void delete(UUID id) {
        if (!fieldRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Field not found");
        }
        fieldRepository.deleteById(id);
    }
}
