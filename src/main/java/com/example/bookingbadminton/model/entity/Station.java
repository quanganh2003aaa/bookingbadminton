package com.example.bookingbadminton.model.entity;

import com.example.bookingbadminton.model.BaseModel;
import com.example.bookingbadminton.model.Enum.ActiveStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Station extends BaseModel {
    private UUID idOwner;
    private String name;
    private String address;
    private String ratePoint;
    private String msisdn;
    private String mobileContact;
    private String startTime;
    private String endTime;

    @Enumerated(EnumType.STRING)
    private ActiveStatus active;

    private String linkMap;
}
