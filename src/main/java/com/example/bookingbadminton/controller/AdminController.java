package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.model.Enum.RegisterStatus;
import com.example.bookingbadminton.model.entity.Admin;
import com.example.bookingbadminton.payload.*;
import com.example.bookingbadminton.service.AdminService;
import com.example.bookingbadminton.service.FieldService;
import com.example.bookingbadminton.service.RegisterOwnerService;
import com.example.bookingbadminton.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final FieldService fieldService;
    private final RegisterOwnerService registerOwnerService;

    //API admin danh sách tài khoản người dùng
    @GetMapping("/manage-user")
    public ApiResponse adminList(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) String search,
                                 @RequestParam(required = false) Boolean locked) {

        return ApiResponse.builder().result(PageResponse.from(userService.adminList(search, locked, page, size))).build();
    }

    //API admin danh sách sân cầu
    @GetMapping("/manage-field")
    public ApiResponse adminList(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FieldAdminResponse> result = fieldService.adminList(search, pageable);
        return ApiResponse.builder().result(PageResponse.from(result)).build();
    }

    //API admin danh sách đơn đăng ký quản lý
    @GetMapping("/manage-register-owner")
    public ApiResponse adminList(@RequestParam(required = false) RegisterStatus status,
                                 @RequestParam(required = false) String search) {
        return ApiResponse.builder()
                .result(registerOwnerService.adminList(status, search))
                .build();
    }

    //API admin chấp thuận đơn đăng ký quản lý
    @PostMapping("/{id}/approve")
    public ApiResponse approve(@PathVariable UUID id) {
        return ApiResponse.builder().result(registerOwnerService.approve(id)).build();
    }

    //API admin không chấp thuận đơn đăng ký quản lý
    @PostMapping("/{id}/reject")
    public ApiResponse reject(@PathVariable UUID id) {
        RegisterOwnerRejectResponse result = registerOwnerService.reject(id);
        return ApiResponse.builder().result(result).build();
    }

    //API admin chi tiết đơn đăng ký quản lý
    @GetMapping("/{id}/detail-register-owner")
    public ApiResponse detailRegisterOwner(@PathVariable UUID id) {
        RegisterOwnerDetailResponse result = registerOwnerService.detail(id);
        return ApiResponse.builder().result(result).build();
    }

    //API admin chi tiết sân cầu
    @GetMapping("/{id}/detail-field")
    public ApiResponse detailFieldAdmin(@PathVariable UUID id) {
        FieldDetailResponse detail = fieldService.detail(id);
        return ApiResponse.builder().result(detail).build();
    }

    public record CreateAdminRequest(UUID accountId, String name) {
    }
}
