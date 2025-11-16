package com.example.bookingbadminton.model.entity;

import com.example.bookingbadminton.model.BaseModel;
import com.example.bookingbadminton.model.Enum.LevelUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseModel {
    private String password;
    @Column(unique = true)
    private String gmail;
    @Column(unique = true)
    private String msisdn;
    private String name;
    @Enumerated(EnumType.STRING)
    private LevelUser level;
    private Float rateLevel;
    private String avatar;
}
