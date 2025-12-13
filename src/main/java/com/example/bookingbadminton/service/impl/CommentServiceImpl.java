package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.entity.Comment;
import com.example.bookingbadminton.model.entity.Field;
import com.example.bookingbadminton.model.entity.User;
import com.example.bookingbadminton.repository.CommentRepository;
import com.example.bookingbadminton.repository.FieldRepository;
import com.example.bookingbadminton.repository.UserRepository;
import com.example.bookingbadminton.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final FieldRepository fieldRepository;
    private final UserRepository userRepository;

    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Override
    public Comment get(UUID id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
    }

    @Override
    public Comment create(UUID fieldId, UUID userId, Float rate, String content) {
        Comment comment = new Comment();
        return saveComment(comment, fieldId, userId, rate, content);
    }

    @Override
    public Comment update(UUID id, UUID fieldId, UUID userId, Float rate, String content) {
        Comment comment = get(id);
        return saveComment(comment, fieldId, userId, rate, content);
    }

    private Comment saveComment(Comment comment, UUID fieldId, UUID userId, Float rate, String content) {
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Field not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        comment.setField(field);
        comment.setUser(user);
        comment.setRate(rate);
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    @Override
    public void delete(UUID id) {
        if (!commentRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
        }
        commentRepository.deleteById(id);
    }
}
