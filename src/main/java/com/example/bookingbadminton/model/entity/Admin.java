package com.example.bookingbadminton.model.entity;

import com.example.bookingbadminton.model.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "admin")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Admin extends BaseModel {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_account", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Account account;

    @Column(length = 50, nullable = false)
    private String name;
}
