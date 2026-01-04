package com.example.bookingbadminton.model.entity;

import com.example.bookingbadminton.model.BaseModel;
import com.example.bookingbadminton.model.Enum.RegisterStatus;
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

@Entity
@Table(name = "register_owner")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterOwner extends BaseModel {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_account", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Account account;

    @Column(length = 255, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "mobile_contact", length = 10)
    private String mobileContact;

    @Column(length = 50)
    private String gmail;

    @Enumerated(EnumType.STRING)
    private RegisterStatus active;

    @Column(name = "link_map", columnDefinition = "TEXT")
    private String linkMap;

    @Column(name = "img_qr", columnDefinition = "TEXT")
    private String imgQr;
}
