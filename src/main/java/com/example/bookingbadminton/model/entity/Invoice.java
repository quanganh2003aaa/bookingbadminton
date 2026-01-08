package com.example.bookingbadminton.model.entity;

import com.example.bookingbadminton.model.BaseModel;
import com.example.bookingbadminton.model.Enum.InvoiceStatus;
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

@Entity
@Table(name = "invoice")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice extends BaseModel {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_booking", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Booking booking;

    private Integer price;

    private String imgPayment;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;
}
