package com.example.bookingbadminton.service;

import com.example.bookingbadminton.payload.DetailInfoUserResp;
import com.example.bookingbadminton.payload.UserAdminResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface UserService {
    DetailInfoUserResp getDetailInfoUser(UUID ownerId);

    Page<UserAdminResponse> adminList(String search, Boolean locked, int page, int size);
}
