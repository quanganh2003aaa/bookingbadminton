package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.model.entity.Comment;
import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ApiResponse list() {
        return ApiResponse.builder().result(commentService.findAll()).build();
    }

    @GetMapping("/{id}")
    public ApiResponse get(@PathVariable UUID id) {
        return ApiResponse.builder()
                .result(commentService.get(id))
                .build();
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody CommentRequest request) {
        Comment saved = commentService.create(request.fieldId(), request.userId(), request.rate(), request.content());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder().result(saved).build());
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable UUID id, @RequestBody CommentRequest request) {
        return ApiResponse.builder()
                .result(commentService.update(id, request.fieldId(), request.userId(), request.rate(), request.content()))
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record CommentRequest(UUID fieldId, UUID userId, Float rate, String content) {
    }
}
