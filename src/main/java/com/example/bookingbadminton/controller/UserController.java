package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.payload.DetailInfoUserResp;
import com.example.bookingbadminton.payload.request.ValidUserRequest;
import com.example.bookingbadminton.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //TODO API thông tin tài khoản user
    @PostMapping ("/detail-info")
    public ApiResponse getInfoDetail(@RequestBody ValidUserRequest request) {
        DetailInfoUserResp result = userService.getDetailInfoUser(request.userId());
        return ApiResponse.builder().result(result).build();
    }

}
