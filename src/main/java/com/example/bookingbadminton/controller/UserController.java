package com.example.bookingbadminton.controller;

import com.example.bookingbadminton.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/test")
    public ResponseEntity<?> getAllCategory(){
        ApiResponse api = new ApiResponse();
//        api.setResult(categoryServiceImp.getListCategory());
        return new ResponseEntity<>(api, HttpStatus.OK);
    }
}
