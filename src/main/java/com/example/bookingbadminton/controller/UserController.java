package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.payload.UserRequest;
import com.example.bookingbadminton.service.AuthenticationService;
import com.example.bookingbadminton.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @GetMapping
    public ApiResponse list() {
        return ApiResponse.builder().result(userService.findAll()).build();
    }


    @GetMapping("/{id}")
    public ApiResponse get(@PathVariable UUID id) {
        return ApiResponse.builder().result(userService.get(id)).build();
    }

//    @PostMapping
//    public ResponseEntity<ApiResponse> create(@RequestBody @Valid UserRequest request) {
//        User saved = userService.create(request);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(ApiResponse.builder().result(saved).build());
//    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable UUID id, @RequestBody @Valid UserRequest request) {
        return ApiResponse.builder().result(userService.update(id, request)).build();
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable UUID id) {
//        accountService.delete(id);
//        return ResponseEntity.noContent().build();
//    }

}
