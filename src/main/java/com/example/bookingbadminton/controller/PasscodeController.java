package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.payload.request.RegisterOwnerPasscodeRequest;
import com.example.bookingbadminton.service.PasscodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/passcodes")
@RequiredArgsConstructor
public class PasscodeController {

    private final PasscodeService passcodeService;

//    @PostMapping("/register-owner")
//    public ApiResponse createRegisterOwnerPasscode(@RequestBody @Valid RegisterOwnerPasscodeRequest request) {
//        return ApiResponse.builder().result(passcodeService.createRegisterOwnerPasscode(request)).build();
//    }

//    @GetMapping
//    public ApiResponse list() {
//        return ApiResponse.builder().result(passcodeService.findAll()).build();
//    }
//
//    @GetMapping("/{id}")
//    public ApiResponse get(@PathVariable UUID id) {
//        return ApiResponse.builder()
//                .result(passcodeService.get(id))
//                .build();
//    }
//
//    @PostMapping
//    public ResponseEntity<ApiResponse> create(@RequestBody @Valid PasscodeRequest request) {
//        Passcode saved = passcodeService.create(
//                request.accountId(),
//                request.code(),
//                request.type()
//        );
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(ApiResponse.builder().result(saved).build());
//    }
//
//    @PutMapping("/{id}")
//    public ApiResponse update(@PathVariable UUID id, @RequestBody @Valid PasscodeRequest request) {
//        return ApiResponse.builder()
//                .result(passcodeService.update(
//                        id,
//                        request.accountId(),
//                        request.code(),
//                        request.type(),
//                        request.active(),
//                        request.time(),
//                        request.totalDay(),
//                        request.totalMonth()
//                ))
//                .build();
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable UUID id) {
//        passcodeService.delete(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    public record PasscodeRequest(
//            @NotNull UUID accountId,
//            @NotBlank @Size(min = 6, max = 6) @Pattern(regexp = "\\d{6}") String code,
//            @NotNull TypePasscode type,
//            @NotNull ActiveStatus active,
//            LocalDateTime time,
//            Integer totalDay,
//            Integer totalMonth
//    ) {
//    }




}
