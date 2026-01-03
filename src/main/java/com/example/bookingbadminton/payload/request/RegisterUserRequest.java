package com.example.bookingbadminton.payload.request;

import com.example.bookingbadminton.payload.CreateAccountRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(@Valid CreateAccountRequest account,
                                  @NotBlank @Size(max = 50) String name) {}
