package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.entity.Field;
import com.example.bookingbadminton.model.entity.FieldImage;
import com.example.bookingbadminton.model.entity.Owner;
import com.example.bookingbadminton.payload.FieldCardResponse;
import com.example.bookingbadminton.payload.FieldRequest;
import com.example.bookingbadminton.repository.AccountRepository;
import com.example.bookingbadminton.repository.FieldRepository;
import com.example.bookingbadminton.repository.FieldImageRepository;
import com.example.bookingbadminton.repository.OwnerRepository;
import com.example.bookingbadminton.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FieldServiceImpl implements FieldService {

    private final FieldRepository fieldRepository;
    private final AccountRepository accountRepository;
    private final OwnerRepository ownerRepository;
    private final FieldImageRepository fieldImageRepository;

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
    public Field create(FieldRequest request) {
        return saveField(new Field(), request);
    }

    @Override
    public Field update(UUID id, FieldRequest request) {
        return saveField(get(id), request);
    }

    private Field saveField(Field field, FieldRequest request) {
        Owner owner = ownerRepository.findById(request.ownerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found"));
        field.setOwner(owner);
        field.setName(request.name());
        field.setAddress(request.address());
        field.setRatePoint(request.ratePoint());
        field.setMsisdn(request.msisdn());
        field.setMobileContact(request.mobileContact());
        field.setStartTime(request.startTime());
        field.setEndTime(request.endTime());
        field.setActive(request.active());
        field.setLinkMap(request.linkMap());
        return fieldRepository.save(field);
    }

    @Override
    public void delete(UUID id) {
        Field field = get(id);
        field.setDeletedAt(LocalDateTime.now());
        fieldRepository.save(field);
    }

    @Override
    public Page<FieldCardResponse> search(String search, Pageable pageable) {
        Page<Field> page = (search == null || search.isBlank())
                ? fieldRepository.findAll(pageable)
                : fieldRepository.findByNameContainingIgnoreCase(search, pageable);
        return page.map(this::toCardResponse);
    }

    private FieldCardResponse toCardResponse(Field field) {
        String image = fieldImageRepository.findFirstByField_Id(field.getId())
                .map(FieldImage::getImage)
                .orElse(null);
        return new FieldCardResponse(
                field.getId(),
                field.getName(),
                field.getAddress(),
                field.getStartTime(),
                field.getEndTime(),
                field.getMobileContact(),
                image
        );
    }
}
