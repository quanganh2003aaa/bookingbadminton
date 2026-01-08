package com.example.bookingbadminton.model.entity;

import com.example.bookingbadminton.model.BaseModel;
import com.example.bookingbadminton.model.Enum.ActiveStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Entity
@JsonIgnoreProperties({"parentField", "subFields", "bookingField", "owner", "hibernateLazyInitializer", "handler"})
@Table(name = "field")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Field extends BaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_owner", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Owner owner;

    @Column(length = 255, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "rate_point")
    private Float ratePoint;

    @Column(length = 10)
    private String msisdn;

    @Column(name = "mobile_contact", length = 10)
    private String mobileContact;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "index_field")
    @Builder.Default
    private Integer indexField = 0;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "img_qr", columnDefinition = "LONGTEXT")
    private String imgQr;

    @Enumerated(EnumType.STRING)
    private ActiveStatus active;

    @Column(name = "link_map", columnDefinition = "TEXT")
    private String linkMap;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    private List<BookingField> bookingField;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Field parentField;

    @OneToMany(mappedBy = "parentField", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Field> subFields;
}
