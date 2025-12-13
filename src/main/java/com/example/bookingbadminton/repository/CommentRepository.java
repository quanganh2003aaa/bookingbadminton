package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
}
