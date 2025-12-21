package com.example.bookingbadminton.model.dto;

import com.example.bookingbadminton.model.entity.TimeSlot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotDTO {
    private Integer price;
    private LocalTime endHour;
    private LocalTime startHour;

    public TimeSlotDTO(TimeSlot timeSlot){
        this.price = timeSlot.getPrice();
        this.endHour = timeSlot.getEndHour();
        this.startHour = timeSlot.getStartHour();
    }
}
