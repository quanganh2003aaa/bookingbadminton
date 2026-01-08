package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.model.Enum.BookingStatus;
import com.example.bookingbadminton.model.Enum.InvoiceStatus;
import com.example.bookingbadminton.model.entity.Booking;
import com.example.bookingbadminton.model.entity.Invoice;
import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.payload.BookingByDayResponse;
import com.example.bookingbadminton.payload.FieldOwnerDailyBookingResponse;
import com.example.bookingbadminton.payload.TempBookingRequest;
import com.example.bookingbadminton.payload.TempBookingResponse;
import com.example.bookingbadminton.service.BookingService;
import com.example.bookingbadminton.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final InvoiceService invoiceService;

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

//    @GetMapping
//    public ApiResponse list() {
//        return ApiResponse.builder().result(bookingService.findAll()).build();
//    }

    @GetMapping("/{id}")
    public ApiResponse get(@PathVariable UUID id) {
        return ApiResponse.builder()
                .result(bookingService.get(id))
                .build();
    }



//    @PostMapping
//    public ResponseEntity<ApiResponse> create(@RequestBody BookingRequest request) {
//        Booking saved = bookingService.create(
//                request.fieldId(),
//                request.userId(),
//                request.msisdn(),
//                request.indexField(),
//                request.startHour(),
//                request.endHour(),
//                request.status()
//        );
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(ApiResponse.builder().result(saved).build());
//    }
//
//    @PostMapping("/batch")
//    public ResponseEntity<ApiResponse> createBatch(@RequestBody BookingBatchRequest request) {
//        List<Booking> saved = request.items().stream()
//                .map(item -> bookingService.create(
//                        request.fieldId(),
//                        request.userId(),
//                        request.msisdn(),
//                        item.indexField(),
//                        item.startHour(),
//                        item.endHour(),
//                        request.status()
//                ))
//                .toList();
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(ApiResponse.builder().result(saved).build());
//    }
//
//    @PostMapping("/with-invoice")
//    public ResponseEntity<ApiResponse> createWithInvoice(@RequestBody BookingWithInvoiceRequest request) {
//        Booking saved = bookingService.create(
//                request.fieldId(),
//                request.userId(),
//                request.msisdn(),
//                request.indexField(),
//                request.startHour(),
//                request.endHour(),
//                request.status()
//        );
//        Invoice invoice = invoiceService.create(saved.getId(), request.price(), request.invoiceStatus());
//        var response = java.util.Map.of(
//                "booking", saved,
//                "invoice", invoice
//        );
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(ApiResponse.builder().result(response).build());
//    }
//
//    @PutMapping("/{id}")
//    public ApiResponse update(@PathVariable UUID id, @RequestBody BookingRequest request) {
//        return ApiResponse.builder()
//                .result(bookingService.update(
//                        id,
//                        request.fieldId(),
//                        request.userId(),
//                        request.msisdn(),
//                        request.indexField(),
//                        request.startHour(),
//                        request.endHour(),
//                        request.status()
//                ))
//                .build();
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable UUID id) {
//        bookingService.delete(id);
//        return ResponseEntity.noContent().build();
//    }

//    @GetMapping("/by-day")
//    public ApiResponse findByDay(@RequestParam java.time.LocalDate date) {
//        return ApiResponse.builder()
//                .result(bookingService.findByDay(date))
//                .build();
//    }
//
//    @GetMapping("/field/{fieldId}/by-day")
//    public ApiResponse findByDayAndField(@PathVariable UUID fieldId,
//                                         @RequestParam java.time.LocalDate date) {
//        return ApiResponse.builder()
//                .result(bookingService.findByDayAndField(date, fieldId))
//                .build();
//    }

    public record BookingRequest(
            UUID fieldId,
            UUID userId,
            String msisdn,
            Integer indexField,
            LocalDateTime startHour,
            LocalDateTime endHour,
            BookingStatus status
    ) {
    }

    public record BookingWithInvoiceRequest(
            UUID fieldId,
            UUID userId,
            String msisdn,
            Integer indexField,
            LocalDateTime startHour,
            LocalDateTime endHour,
            BookingStatus status,
            Integer price,
            InvoiceStatus invoiceStatus
    ) {
    }

    public record BookingBatchRequest(
            UUID fieldId,
            UUID userId,
            String msisdn,
            BookingStatus status,
            List<BookingItem> items
    ) {}

    public record BookingItem(
            Integer indexField,
            LocalDateTime startHour,
            LocalDateTime endHour
    ) {}
}
