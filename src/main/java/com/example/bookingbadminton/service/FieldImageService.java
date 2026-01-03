package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.Enum.TypeImage;
import com.example.bookingbadminton.model.entity.FieldImage;

import java.util.List;
import java.util.UUID;

public interface FieldImageService {
    List<FieldImage> findAll();

    FieldImage get(UUID id);

    FieldImage create(UUID fieldId, TypeImage type, String image);

    FieldImage update(UUID id, UUID fieldId, TypeImage type, String image);

    void delete(UUID id);

    FieldImage upload(UUID fieldId, TypeImage type, org.springframework.web.multipart.MultipartFile file);

    List<String> listByField(UUID fieldId);
}
