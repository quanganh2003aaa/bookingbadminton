package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.entity.Field;
import com.example.bookingbadminton.payload.FieldCardResponse;
import com.example.bookingbadminton.payload.FieldRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface FieldService {
    List<Field> findAll();

    Field get(UUID id);

    Field create(FieldRequest request);

    Field update(UUID id, FieldRequest request);

    void delete(UUID id);

    Page<FieldCardResponse> search(String search, Pageable pageable);
}
