package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.entity.Comment;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    List<Comment> findAll();

    Comment get(UUID id);

    Comment create(UUID fieldId, UUID userId, Float rate, String content);

    Comment update(UUID id, UUID fieldId, UUID userId, Float rate, String content);

    void delete(UUID id);
}
