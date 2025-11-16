package com.example.bookingbadminton.model.entity;

import com.example.bookingbadminton.model.BaseModel;
import com.example.bookingbadminton.model.Enum.LevelUser;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseModel {
    private String username;
    private String password;
    private String gmail;
    private String msisdn;
    private String name;
    private LevelUser level;
    private Float rateLevel;
    private String avatar;
}
