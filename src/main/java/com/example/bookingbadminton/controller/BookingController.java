package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.payload.FieldOwnerDailyBookingResponse;
import com.example.bookingbadminton.payload.TempBookingRequest;
import com.example.bookingbadminton.payload.TempBookingResponse;
import com.example.bookingbadminton.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    //API chi tiết booking của 1 sân cha
    @GetMapping("/field/{fieldId}/by-day")
    public ApiResponse bookingsByDay(@PathVariable UUID fieldId, @RequestParam LocalDate date) {
        FieldOwnerDailyBookingResponse result = bookingService.bookingsByDayForField(fieldId, date);
        return ApiResponse.builder().result(result).build();
    }

    //API tạo booking tạm thời
    @PostMapping("/pending")
    public ApiResponse createTempPending(@RequestBody TempBookingRequest request) {
        TempBookingResponse result = bookingService.createTempPendingBooking(request);
        return ApiResponse.builder().result(result).build();
    }

    @GetMapping("/{id}/pay-detail")
    public ApiResponse paidDetail(@PathVariable UUID id) {
        return ApiResponse.builder()
                .result(bookingService.paidBookingDetail(id))
                .build();
    }

    @GetMapping("/user/{userId}/list")
    public ApiResponse userBookings(@PathVariable UUID userId) {
        return ApiResponse.builder().result(bookingService.userBookings(userId)).build();
    }

    @GetMapping("/user/{bookingId}/detail")
    public ApiResponse userBookingDetail(@PathVariable UUID bookingId) {
        return ApiResponse.builder().result(bookingService.userBookingDetail(bookingId)).build();
    }

    @PostMapping(value = "/paying/{bookingId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse paying(@PathVariable String bookingId, @RequestPart("file") MultipartFile file) {
        String result = bookingService.paying(UUID.fromString(bookingId), file);
        return ApiResponse.builder().result(result).build();
    }
}
