package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.payload.FieldCardResponse;
import com.example.bookingbadminton.payload.FieldUserDetailResponse;
import com.example.bookingbadminton.payload.FieldRequest;
import com.example.bookingbadminton.payload.PageResponse;
import com.example.bookingbadminton.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/fields")
@RequiredArgsConstructor
public class FieldController {

    private final FieldService fieldService;

    //TODO API danh sách sân (trang chủ) - PUBLIC
    @GetMapping
    public ApiResponse listFieldHomePage(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(required = false) String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FieldCardResponse> result = fieldService.search(search, pageable);
        return ApiResponse.builder().result(PageResponse.from(result)).build();
    }

    //TODO API chi tiết sân (trang chủ) - PUBLIC
    @GetMapping("/{id}/detail")
    public ApiResponse detailFieldHomePage(@PathVariable UUID id) {
        FieldUserDetailResponse detail = fieldService.userDetail(id);
        return ApiResponse.builder().result(detail).build();
    }

    //TODO API cập nhật thông tin sân
    @PutMapping("/{id:[0-9a-fA-F\\-]{36}}/owner")
    public ApiResponse ownerUpdateField(@PathVariable UUID id,
                                   @RequestBody FieldRequest request) {
        return ApiResponse.builder().result(fieldService.ownerUpdateField(id, request)).build();
    }

    @GetMapping("/{id}/quantity")
    public ApiResponse quantity(@PathVariable UUID id) {
        var field = fieldService.get(id);
        return ApiResponse.builder().result(new QuantityResponse(field.getQuantity())).build();
    }

    public record QuantityResponse(Integer quantity) {}
}
