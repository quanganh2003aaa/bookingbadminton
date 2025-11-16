package com.example.bookingbadminton.model.entity;

import com.example.bookingbadminton.model.BaseModel;
import com.example.bookingbadminton.model.Enum.StationStatus;
import jakarta.persistence.Entity;
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
    private StationStatus active;
    private String linkMap;
}
