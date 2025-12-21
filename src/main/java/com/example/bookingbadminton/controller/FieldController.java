package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.model.Enum.ActiveStatus;
import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.payload.FieldCardResponse;
import com.example.bookingbadminton.payload.FieldRequest;
import com.example.bookingbadminton.payload.PageResponse;
import com.example.bookingbadminton.payload.FieldAdminResponse;
import com.example.bookingbadminton.payload.FieldDetailResponse;
import com.example.bookingbadminton.payload.FieldOwnerSummaryResponse;
import com.example.bookingbadminton.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/fields")
@RequiredArgsConstructor
public class FieldController {

    private final FieldService fieldService;

    @GetMapping
    public ApiResponse list(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(required = false) String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FieldCardResponse> result = fieldService.search(search, pageable);
        return ApiResponse.builder().result(PageResponse.from(result)).build();
    }

    @GetMapping("/{id}")
    public ApiResponse get(@PathVariable UUID id) {
        return ApiResponse.builder().result(fieldService.get(id)).build();
    }

    @GetMapping("/{id}/detail")
    public ApiResponse detail(@PathVariable UUID id) {
        FieldDetailResponse detail = fieldService.detail(id);
        return ApiResponse.builder().result(detail).build();
    }

    @GetMapping("/owner")
    public ApiResponse ownerList(@RequestParam UUID ownerId,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FieldOwnerSummaryResponse> result = fieldService.ownerFields(ownerId, pageable);
        return ApiResponse.builder().result(PageResponse.from(result)).build();
    }

    @GetMapping("/owner/{id}")
    public ApiResponse ownerDetail(@PathVariable UUID id, @RequestParam UUID ownerId) {
        FieldDetailResponse detail = fieldService.ownerFieldDetail(ownerId, id);
        return ApiResponse.builder().result(detail).build();
    }

    @PutMapping("/owner/{id}")
    public ApiResponse ownerUpdate(@PathVariable UUID id,
                                   @RequestParam UUID ownerId,
                                   @RequestBody FieldRequest request) {
        return ApiResponse.builder().result(fieldService.ownerUpdate(ownerId, id, request)).build();
    }

    @GetMapping("/admin")
    public ApiResponse adminList(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FieldAdminResponse> result = fieldService.adminList(search, pageable);
        return ApiResponse.builder().result(PageResponse.from(result)).build();
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody FieldRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .result(fieldService.create(r))
                .build());
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable UUID id, @RequestBody FieldRequest request) {
        return ApiResponse.builder().result(fieldService.update(id, request)).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        fieldService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
