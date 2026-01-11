package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.payload.*;
import com.example.bookingbadminton.payload.request.ValidOwnerAndFieldRequest;
import com.example.bookingbadminton.payload.request.ValidOwnerRequest;
import com.example.bookingbadminton.service.BookingService;
import com.example.bookingbadminton.service.FieldService;
import com.example.bookingbadminton.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/owner")
@RequiredArgsConstructor
public class OwnerController {
    private final FieldService fieldService;
    private final BookingService bookingService;
    private final OwnerService ownerService;

    //TODO API danh sách sân sở hữu (quản lý sân)
    @GetMapping("/fields")
    public ApiResponse ownerList(@RequestParam UUID ownerId,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FieldOwnerSummaryResponse> result = fieldService.ownerFields(ownerId, pageable);
        return ApiResponse.builder().result(PageResponse.from(result)).build();
    }

    //TODO API owner chi tiết sân
    @PostMapping("/detail-field/{id:[0-9a-fA-F\\-]{36}}")
    public ApiResponse ownerDetail(@PathVariable UUID id, @RequestBody ValidOwnerRequest request) {
        FieldOwnerDetailResponse detail = fieldService.ownerFieldDetail(request, id);
        return ApiResponse.builder().result(detail).build();
    }

    //TODO API danh sách sân sở hữu (tình trạng sân)
    @GetMapping("/field-booking")
    public ApiResponse ownerBookingSummary(@RequestParam UUID ownerId,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FieldOwnerBookingSummary> result = fieldService.ownerFieldBookings(ownerId, pageable);
        return ApiResponse.builder().result(PageResponse.from(result)).build();
    }

    //TODO API chi tiết tình trạng sân
    @GetMapping ("/field-booking/{id:[0-9a-fA-F\\-]{36}}")
    public ApiResponse ownerDailyBookings(@PathVariable UUID id,
                                          @RequestParam LocalDate date) {
        FieldOwnerDailyBookingResponse result = fieldService.ownerDailyBookings(id, date);
        return ApiResponse.builder().result(result).build();
    }

    //TODO Danh sách booking của sân cha (gộp booking_field, bỏ pending quá 5p)
    @GetMapping("/field-booking/{id:[0-9a-fA-F\\-]{36}}/list-booking")
    public ApiResponse ownerListBookings(@PathVariable UUID id,
                                         @RequestParam LocalDate date) {
        return ApiResponse.builder().result(bookingService.ownerListBookings(id, date)).build();
    }

    // Chấp thuận đơn đặt sân
    @PostMapping("/approve-booking/{id}")
    public ApiResponse approveBooking(@PathVariable UUID id, @RequestBody ValidOwnerAndFieldRequest request) {
        bookingService.approveBooking(id, request);
        return ApiResponse.builder().message("Chấp thuận đơn đặt sân thành công.").build();
    }

    //TODO API từ chối đơn đặt sân
    @PostMapping("/reject-booking/{id}")
    public ApiResponse reject(@PathVariable UUID id, @RequestBody ValidOwnerAndFieldRequest request) {
        bookingService.rejectBooking(id, request);
        return ApiResponse.builder().message("Từ chối đơn đặt sân thành công.").build();
    }

    //TODO API thông tin tài khoản owner
    @PostMapping ("/detail-info")
    public ApiResponse getInfoDetail(@RequestBody ValidOwnerRequest request) {
        DetailInfoOwnerResp result = ownerService.getDetailInfoOwner(request.ownerId());
        return ApiResponse.builder().result(result).build();
    }
}
