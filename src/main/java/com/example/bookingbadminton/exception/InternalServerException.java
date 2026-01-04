package com.example.bookingbadminton.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InternalServerException extends RuntimeException {
    public InternalServerException(String message) {
        super(message);
    }
}