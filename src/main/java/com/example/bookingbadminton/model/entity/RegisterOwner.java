package com.example.bookingbadminton.model.entity;

import com.example.bookingbadminton.model.BaseModel;
import com.example.bookingbadminton.model.Enum.RegisterStatus;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterOwner extends BaseModel {
    private UUID idOwner;
    private String name;
    private String address;
    private String mobileContact;
    private String gmail;
    private RegisterStatus active;
    private String linkMap;
}
