package com.example.bookingbadminton.model.dto.response.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountResponseDto {
    private String password;

    private String gmail;

    private String msisdn;
}
