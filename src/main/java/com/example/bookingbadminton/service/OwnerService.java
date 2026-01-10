package com.example.bookingbadminton.service;

import com.example.bookingbadminton.payload.DetailInfoOwnerResp;

import java.util.UUID;

public interface OwnerService {
    DetailInfoOwnerResp getDetailInfoOwner(UUID ownerId);
}
