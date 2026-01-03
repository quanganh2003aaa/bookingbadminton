package com.example.bookingbadminton.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "BookingField")
@Table(name = "booking_field")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingField {
    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", insertable = false, updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Field field;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Booking booking;

    @Column(name = "start_hour")
    private LocalDateTime startHour;

    @Column(name = "end_hour")
    private LocalDateTime endHour;
}
