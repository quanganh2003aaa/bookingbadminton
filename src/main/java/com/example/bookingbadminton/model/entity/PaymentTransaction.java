package com.example.bookingbadminton.model.entity;

import com.example.bookingbadminton.model.Enum.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity(name = "PaymentTransaction")
@Table(name = "payment_transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {
    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", insertable = false, updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    String id;

    @Column(name = "amount", nullable = false)
    Double amount;

    @UpdateTimestamp
    LocalDateTime expiredDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_booking", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    Booking booking;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    PaymentStatus status;
}
