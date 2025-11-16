package com.example.bookingbadminton.model.entity;

import com.example.bookingbadminton.model.BaseModel;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseModel {
    private UUID idUser;
    private UUID idStation;
    private Float rate;
    private String content;
}
