package com.example.bookingbadminton.model.entity;

import com.example.bookingbadminton.model.BaseModel;
import com.example.bookingbadminton.model.Enum.ActiveStatus;
import com.example.bookingbadminton.model.Enum.TypePasscode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "passcode")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Passcode extends BaseModel {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_account", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Account account;

    @Column(length = 8, nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    private TypePasscode type;

    @Enumerated(EnumType.STRING)
    private ActiveStatus active;

    private LocalDateTime time;
    @Column(name = "total_day")
    private Integer totalDay;
    @Column(name = "total_month")
    private Integer totalMonth;
}
