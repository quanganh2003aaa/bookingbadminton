package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.model.Enum.ActiveStatus;
import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.payload.FieldCardResponse;
import com.example.bookingbadminton.payload.FieldAdminResponse;
import com.example.bookingbadminton.payload.FieldOwnerDetailResponse;
import com.example.bookingbadminton.payload.FieldOwnerSummaryResponse;
import com.example.bookingbadminton.payload.FieldOwnerBookingSummary;
import com.example.bookingbadminton.payload.FieldOwnerDailyBookingResponse;
import com.example.bookingbadminton.payload.FieldUserDetailResponse;
import com.example.bookingbadminton.payload.FieldRequest;
import com.example.bookingbadminton.payload.PageResponse;
import com.example.bookingbadminton.model.Enum.TypeImage;
import com.example.bookingbadminton.service.FieldService;
import com.example.bookingbadminton.service.FieldImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/fields")
@RequiredArgsConstructor
public class FieldController {

    private final FieldService fieldService;
    private final FieldImageService fieldImageService;

    //API owner chi tiết sân
    @GetMapping("/owner/{id:[0-9a-fA-F\\-]{36}}")
    public ApiResponse ownerDetail(@PathVariable UUID id, @RequestParam UUID ownerId) {
        FieldOwnerDetailResponse detail = fieldService.ownerFieldDetail(ownerId, id);
        return ApiResponse.builder().result(detail).build();
    }

    //API chi tiết sân - public
    @GetMapping("/{id}/detail")
    public ApiResponse userDetail(@PathVariable UUID id) {
        FieldUserDetailResponse detail = fieldService.userDetail(id);
        return ApiResponse.builder().result(detail).build();
    }

    //API cập nhật thông tin sân
    @PutMapping("/owner/{id:[0-9a-fA-F\\-]{36}}")
    public ApiResponse ownerUpdate(@PathVariable UUID id,
                                   @RequestParam UUID ownerId,
                                   @RequestBody FieldRequest request) {
        return ApiResponse.builder().result(fieldService.ownerUpdate(ownerId, id, request)).build();
    }

    //API owner chi tiết tình trạng sân
    @GetMapping("/{id:[0-9a-fA-F\\-]{36}}/bookings")
    public ApiResponse ownerDailyBookings(@PathVariable UUID id,
                                          @RequestParam UUID ownerId,
                                          @RequestParam LocalDate date) {
        FieldOwnerDailyBookingResponse result = fieldService.ownerDailyBookings(ownerId, id, date);
        return ApiResponse.builder().result(result).build();
    }

    @GetMapping
    public ApiResponse list(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(required = false) String search,
                            @RequestParam(required = false) ActiveStatus active) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FieldCardResponse> result = fieldService.search(search, active, pageable);
        return ApiResponse.builder().result(PageResponse.from(result)).build();
    }
//
//    @GetMapping("/{id}")
//    public ApiResponse get(@PathVariable UUID id) {
//        return ApiResponse.builder().result(fieldService.get(id)).build();
//    }

    @GetMapping("/{id}/quantity")
    public ApiResponse quantity(@PathVariable UUID id) {
        var field = fieldService.get(id);
        return ApiResponse.builder().result(new QuantityResponse(field.getQuantity())).build();
    }







    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody FieldRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .result(fieldService.create(r))
                .build());
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<ApiResponse> addImage(@PathVariable UUID id, @RequestBody FieldImageAddRequest request) {
        var saved = fieldImageService.create(id, request.type(), request.image());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder().result(saved).build());
    }

    @PostMapping("/{id}/images/upload")
    public ResponseEntity<ApiResponse> uploadImage(@PathVariable UUID id,
                                                   @RequestParam(defaultValue = "USING") TypeImage type,
                                                   @RequestParam("file") MultipartFile file) {
        var saved = fieldImageService.upload(id, type, file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder().result(saved).build());
    }

    @GetMapping("/{id}/images")
    public ApiResponse listImages(@PathVariable UUID id) {
        return ApiResponse.builder().result(fieldImageService.listByField(id)).build();
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

    public record FieldImageAddRequest(TypeImage type, String image) {}
    public record QuantityResponse(Integer quantity) {}
}
