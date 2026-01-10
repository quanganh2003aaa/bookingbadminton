package com.example.bookingbadminton.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailInfoOwnerResp {
    private String nameOwner;
    private String msisdn;
    private String email;
    private String avatar;
}
