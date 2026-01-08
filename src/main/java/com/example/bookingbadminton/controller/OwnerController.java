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
import org.springframework.web.bind.annotation.*;

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

    //TODO API chấp thuận đơn đặt sân
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

//    @GetMapping ("/info-detail/{id:[0-9a-fA-F\\-]{36}}")
//    public ApiResponse getInfoDetail(@PathVariable UUID id) {
//        FieldOwnerDailyBookingResponse result = ownerService.ownerDailyBookings(id, date);
//        return ApiResponse.builder().result(result).build();
//    }
}
