package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.Enum.TypeImage;
import com.example.bookingbadminton.model.entity.Field;
import com.example.bookingbadminton.model.entity.FieldImage;
import com.example.bookingbadminton.repository.FieldImageRepository;
import com.example.bookingbadminton.repository.FieldRepository;
import com.example.bookingbadminton.service.FieldImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class FieldImageServiceImpl implements FieldImageService {
    @Value("${file.upload-dir:uploads/fields}")
    private String uploadDir;

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
        FieldImage entity = get(id);
        entity.setDeletedAt(LocalDateTime.now());
        fieldImageRepository.save(entity);
    }

    @Override
    public FieldImage upload(UUID fieldId, TypeImage type, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String filename = UUID.randomUUID() + ext;
        Path dir = Path.of(uploadDir, "fields");
        Path target = dir.resolve(filename);
        try {
            Files.createDirectories(dir);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store file", e);
        }
        // Lưu đường dẫn file vào DB (local path)
        return create(fieldId, type, target.toString());
    }

    @Override
    public List<String> listByField(UUID fieldId) {
        return fieldImageRepository.findByField_Id(fieldId).stream().map(FieldImage::getImage).toList();
    }
}
