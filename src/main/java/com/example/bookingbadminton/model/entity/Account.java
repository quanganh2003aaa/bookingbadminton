package com.example.bookingbadminton.model.entity;

import com.example.bookingbadminton.model.BaseModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Account extends BaseModel {

    @Column(length = 300, nullable = false)
    @JsonIgnore
    private String password;

    @Column(length = 50, nullable = false, unique = true)
    private String gmail;

    @Column(length = 10, nullable = false)
    private String msisdn;

    @Column(name = "keycloak_user_id", length = 64, unique = true)
    private String keycloakUserId;
}
