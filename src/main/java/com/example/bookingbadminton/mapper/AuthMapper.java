package com.example.bookingbadminton.mapper;

import com.example.bookingbadminton.model.entity.RegisterOwner;
import com.example.bookingbadminton.payload.RegisterOwnerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface AuthMapper {
    @Mapping(source = "account.id", target = "accountId")
    RegisterOwnerResponse mapToResponse(RegisterOwner registerOwner);
}
