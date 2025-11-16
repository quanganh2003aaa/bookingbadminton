package com.example.bookingbadminton.model.entity;

import com.example.bookingbadminton.model.Enum.ActiveStatus;
import com.example.bookingbadminton.model.Enum.TypePasscode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Passcode {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String code;

    @Column(unique = true)
    private UUID idUser;

    @Column(unique = true)
    private String gmail;

    @Enumerated(EnumType.STRING)
    private TypePasscode type;

    @Enumerated(EnumType.STRING)
    private ActiveStatus active;

    private LocalDateTime time;
    private Integer totalDay;
    private Integer totalMonth;

}
