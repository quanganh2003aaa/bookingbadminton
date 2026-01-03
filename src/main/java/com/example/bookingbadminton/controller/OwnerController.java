package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.payload.FieldOwnerSummaryResponse;
import com.example.bookingbadminton.payload.PageResponse;
import com.example.bookingbadminton.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/owner")
@RequiredArgsConstructor
public class OwnerController {
    private final FieldService fieldService;

    @GetMapping("/fields")
    public ApiResponse ownerList(@RequestParam UUID ownerId,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FieldOwnerSummaryResponse> result = fieldService.ownerFields(ownerId, pageable);
        return ApiResponse.builder().result(PageResponse.from(result)).build();
    }
}
