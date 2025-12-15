package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.payload.ApiResponse;
import com.example.bookingbadminton.payload.CreateAccountRequest;
import com.example.bookingbadminton.payload.OwnerRequest;
import com.example.bookingbadminton.payload.UserRequest;
import com.example.bookingbadminton.service.AccountService;
import com.example.bookingbadminton.service.OwnerService;
import com.example.bookingbadminton.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;
    private final OwnerService ownerService;

    @GetMapping
    public ApiResponse list() {
        return ApiResponse.builder().result(accountService.findAll()).build();
    }

    @GetMapping("/{id}")
    public ApiResponse get(@PathVariable UUID id) {
        return ApiResponse.builder().result(accountService.get(id)).build();
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody @Valid CreateAccountRequest request) {
        Account saved = accountService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder().result(saved).build());
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable UUID id, @RequestBody Account request) {
        return ApiResponse.builder().result(accountService.update(id, request)).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register/user")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody @Valid RegisterUserRequest request) {
        Account saved = accountService.create(request.account());
        var userPayload = new UserRequest(saved.getId(), request.user().name(), request.user().avatar());
        var user = userService.create(userPayload);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder().result(user).build());
    }

    @PostMapping("/register/owner")
    public ResponseEntity<ApiResponse> registerOwner(@RequestBody @Valid RegisterOwnerRequest request) {
        Account saved = accountService.create(request.account());
        var ownerPayload = new OwnerRequest(saved.getId(), request.owner().name(), request.owner().avatar());
        var owner = ownerService.create(ownerPayload);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder().result(owner).build());
    }

    public record RegisterUserRequest(@Valid CreateAccountRequest account,
                                      @Valid RegisterProfile user) {}
    public record RegisterOwnerRequest(@Valid CreateAccountRequest account,
                                       @Valid RegisterProfile owner) {}

    public record RegisterProfile(@NotBlank @Size(max = 50) String name, String avatar) {}
}
