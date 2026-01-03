package com.example.bookingbadminton.model.entity;

import com.example.bookingbadminton.model.BaseModel;
import com.example.bookingbadminton.model.Enum.BookingStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking extends BaseModel {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_field", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnoreProperties({"owner", "hibernateLazyInitializer", "handler"})
    private Field field;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Column(length = 10)
    private String msisdn;

    @Column(name = "index_field")
    private Integer indexField;

    @Column(name = "start_hour")
    private LocalDateTime startHour;

    @Column(name = "end_hour")
    private LocalDateTime endHour;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
