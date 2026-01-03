package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.Enum.BookingStatus;
import com.example.bookingbadminton.model.entity.Booking;
import com.example.bookingbadminton.model.entity.BookingField;
import com.example.bookingbadminton.model.entity.Field;
import com.example.bookingbadminton.model.entity.User;
import com.example.bookingbadminton.payload.BookingByDayResponse;
import com.example.bookingbadminton.repository.BookingRepository;
import com.example.bookingbadminton.repository.FieldRepository;
import com.example.bookingbadminton.repository.UserRepository;
import com.example.bookingbadminton.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final FieldRepository fieldRepository;
    private final UserRepository userRepository;

    @Override
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking get(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
    }

    @Override
    public Booking create(UUID fieldId, UUID userId, String msisdn, Integer indexField, LocalDateTime startHour, LocalDateTime endHour, BookingStatus status) {
        Booking booking = new Booking();
        return saveBooking(booking, fieldId, userId, msisdn, indexField, startHour, endHour, status);
    }

    @Override
    public Booking update(UUID id, UUID fieldId, UUID userId, String msisdn, Integer indexField, LocalDateTime startHour, LocalDateTime endHour, BookingStatus status) {
        Booking booking = get(id);
        return saveBooking(booking, fieldId, userId, msisdn, indexField, startHour, endHour, status);
    }

    private Booking saveBooking(Booking booking, UUID fieldId, UUID userId, String msisdn, Integer indexField, LocalDateTime startHour, LocalDateTime endHour, BookingStatus status) {
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Field not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        booking.setUser(user);
        booking.setMsisdn(msisdn);
        booking.setStatus(status);
        syncBookingField(booking, field, startHour, endHour);
        return bookingRepository.save(booking);
    }

    @Override
    public void delete(UUID id) {
        Booking booking = get(id);
        booking.setDeletedAt(LocalDateTime.now());
        bookingRepository.save(booking);
    }

    private void syncBookingField(Booking booking, Field field, LocalDateTime startHour, LocalDateTime endHour) {
        List<BookingField> links = booking.getBookingField();
        if (links == null || links.isEmpty()) {
            BookingField link = new BookingField();
            link.setBooking(booking);
            link.setField(field);
            link.setStartHour(startHour);
            link.setEndHour(endHour);
            List<BookingField> newLinks = new ArrayList<>();
            newLinks.add(link);
            booking.setBookingField(newLinks);
            return;
        }
        BookingField link = links.get(0);
        link.setBooking(booking);
        link.setField(field);
        link.setStartHour(startHour);
        link.setEndHour(endHour);
    }

//    @Override
//    public List<BookingByDayResponse> findByDay(java.time.LocalDate date) {
//        LocalDateTime startOfDay = date.atStartOfDay();
//        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
//        return bookingRepository.findByStartHourBetween(startOfDay, endOfDay).stream()
//                .map(b -> new BookingByDayResponse(
//                        b.getId(),
//                        b.getField().getId(),
//                        b.getUser().getId(),
//                        b.getMsisdn(),
//                        b.getIndexField(),
//                        b.getStartHour(),
//                        b.getEndHour(),
//                        b.getStatus()
//                ))
//                .toList();
//    }
//
//    @Override
//    public List<BookingByDayResponse> findByDayAndField(java.time.LocalDate date, UUID fieldId) {
//        LocalDateTime startOfDay = date.atStartOfDay();
//        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
//        return bookingRepository.findByField_IdAndStartHourBetween(fieldId, startOfDay, endOfDay).stream()
//                .map(b -> new BookingByDayResponse(
//                        b.getId(),
//                        b.getField().getId(),
//                        b.getUser().getId(),
//                        b.getMsisdn(),
//                        b.getIndexField(),
//                        b.getStartHour(),
//                        b.getEndHour(),
//                        b.getStatus()
//                ))
//                .toList();
//    }
}
