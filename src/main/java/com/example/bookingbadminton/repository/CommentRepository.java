package com.example.bookingbadminton.repository;

import com.example.bookingbadminton.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    long countByField_Id(UUID fieldId);

    @Query("SELECT COALESCE(AVG(c.rate), 0) FROM Comment c WHERE c.field.id = :fieldId")
    Double averageRateByFieldId(@Param("fieldId") UUID fieldId);

    List<Comment> findByField_IdOrderByCreatedAtDesc(UUID fieldId);
}
