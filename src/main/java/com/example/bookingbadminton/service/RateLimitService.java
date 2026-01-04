package com.example.bookingbadminton.service;

import jakarta.servlet.http.HttpServletRequest;

public interface RateLimitService {
    boolean allowRequest(HttpServletRequest request);
}
