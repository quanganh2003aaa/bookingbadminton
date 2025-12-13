package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.Enum.TypeImage;
import com.example.bookingbadminton.model.entity.Field;
import com.example.bookingbadminton.model.entity.FieldImage;
import com.example.bookingbadminton.repository.FieldImageRepository;
import com.example.bookingbadminton.repository.FieldRepository;
import com.example.bookingbadminton.service.FieldImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FieldImageServiceImpl implements FieldImageService {

    private final FieldImageRepository fieldImageRepository;
    private final FieldRepository fieldRepository;

    @Override
    public List<FieldImage> findAll() {
        return fieldImageRepository.findAll();
    }

    @Override
    public FieldImage get(UUID id) {
        return fieldImageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Field image not found"));
    }

    @Override
    public FieldImage create(UUID fieldId, TypeImage type, String image) {
        FieldImage fieldImage = new FieldImage();
        return saveFieldImage(fieldImage, fieldId, type, image);
    }

    @Override
    public FieldImage update(UUID id, UUID fieldId, TypeImage type, String image) {
        FieldImage existing = get(id);
        return saveFieldImage(existing, fieldId, type, image);
    }

    private FieldImage saveFieldImage(FieldImage entity, UUID fieldId, TypeImage type, String image) {
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Field not found"));
        entity.setField(field);
        entity.setType(type);
        entity.setImage(image);
        return fieldImageRepository.save(entity);
    }

    @Override
    public void delete(UUID id) {
        if (!fieldImageRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Field image not found");
        }
        fieldImageRepository.deleteById(id);
    }
}
