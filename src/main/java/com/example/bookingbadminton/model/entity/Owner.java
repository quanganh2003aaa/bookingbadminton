package com.example.bookingbadminton.model.entity;

import com.example.bookingbadminton.model.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Owner extends BaseModel {
    private String password;
    @Column(unique = true)
    private String gmail;
    @Column(unique = true)
    private String msisdn;
}
