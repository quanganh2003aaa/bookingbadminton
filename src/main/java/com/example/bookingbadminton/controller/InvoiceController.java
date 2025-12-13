package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.model.Enum.InvoiceStatus;
import com.example.bookingbadminton.model.entity.Invoice;
import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    public ApiResponse list() {
        return ApiResponse.builder().result(invoiceService.findAll()).build();
    }

    @GetMapping("/{id}")
    public ApiResponse get(@PathVariable UUID id) {
        return ApiResponse.builder()
                .result(invoiceService.get(id))
                .build();
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody InvoiceRequest request) {
        Invoice saved = invoiceService.create(request.bookingId(), request.price(), request.status());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder().result(saved).build());
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable UUID id, @RequestBody InvoiceRequest request) {
        return ApiResponse.builder()
                .result(invoiceService.update(id, request.bookingId(), request.price(), request.status()))
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        invoiceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record InvoiceRequest(UUID bookingId, Integer price, InvoiceStatus status) {
    }
}
