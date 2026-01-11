package com.example.bookingbadminton.mapper;

import com.example.bookingbadminton.model.entity.RegisterOwner;
import com.example.bookingbadminton.payload.RegisterOwnerResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthMapperImpl implements AuthMapper {

    @Override
    public RegisterOwnerResponse mapToResponse(RegisterOwner registerOwner) {
        if (registerOwner == null) {
            return null;
        }
        UUID accountId = registerOwner.getAccount() != null ? registerOwner.getAccount().getId() : null;
        return RegisterOwnerResponse.builder()
                .id(registerOwner.getId())
                .accountId(accountId)
                .name(registerOwner.getName())
                .address(registerOwner.getAddress())
                .mobileContact(registerOwner.getMobileContact())
                .gmail(registerOwner.getGmail())
                .active(registerOwner.getActive())
                .linkMap(registerOwner.getLinkMap())
                .imgQr(registerOwner.getImgQr())
                .build();
    }
}
