package com.example.bookingbadminton.model.entity;

import com.example.bookingbadminton.model.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account extends BaseModel {

    @Column(length = 300, nullable = false)
    private String password;

    @Column(length = 50, nullable = false, unique = true)
    private String gmail;

    @Column(length = 10, nullable = false, unique = true)
    private String msisdn;
}
