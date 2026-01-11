package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.Enum.BookingStatus;
import com.example.bookingbadminton.model.entity.Booking;
import com.example.bookingbadminton.payload.FieldOwnerBookingListResponse;
import com.example.bookingbadminton.payload.FieldOwnerDailyBookingResponse;
import com.example.bookingbadminton.payload.PaidBookingDetailResponse;
import com.example.bookingbadminton.payload.TempBookingRequest;
import com.example.bookingbadminton.payload.TempBookingResponse;
import com.example.bookingbadminton.payload.request.ValidOwnerAndFieldRequest;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingService {
    void approveBooking(UUID bookingId, ValidOwnerAndFieldRequest request);
    void rejectBooking(UUID bookingId, ValidOwnerAndFieldRequest request);
    Booking get(UUID id);

    FieldOwnerDailyBookingResponse bookingsByDayForField(UUID fieldId, LocalDate date);

    TempBookingResponse createTempPendingBooking(TempBookingRequest request);

    String paying(UUID bookingId, MultipartFile file);

    List<FieldOwnerBookingListResponse> ownerListBookings(UUID parentFieldId, LocalDate date);

    PaidBookingDetailResponse paidBookingDetail(UUID bookingId);

//    List<BookingByDayResponse> findByDay(java.time.LocalDate date);
//
//    List<BookingByDayResponse> findByDayAndField(java.time.LocalDate date, UUID fieldId);
}
