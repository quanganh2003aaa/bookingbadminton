package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.dto.VirusScanResult;
import org.springframework.web.multipart.MultipartFile;

public interface VirusScanService {
    VirusScanResult scan(MultipartFile file);
}
