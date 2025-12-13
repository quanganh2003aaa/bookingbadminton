package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.Enum.BookingStatus;
import com.example.bookingbadminton.model.entity.Booking;
import com.example.bookingbadminton.model.entity.Field;
import com.example.bookingbadminton.model.entity.User;
import com.example.bookingbadminton.repository.BookingRepository;
import com.example.bookingbadminton.repository.FieldRepository;
import com.example.bookingbadminton.repository.UserRepository;
import com.example.bookingbadminton.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
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
    public Booking create(UUID fieldId, UUID userId, String msisdn, LocalDateTime startHour, LocalDateTime endHour, BookingStatus status) {
        Booking booking = new Booking();
        return saveBooking(booking, fieldId, userId, msisdn, startHour, endHour, status);
    }

    @Override
    public Booking update(UUID id, UUID fieldId, UUID userId, String msisdn, LocalDateTime startHour, LocalDateTime endHour, BookingStatus status) {
        Booking booking = get(id);
        return saveBooking(booking, fieldId, userId, msisdn, startHour, endHour, status);
    }

    private Booking saveBooking(Booking booking, UUID fieldId, UUID userId, String msisdn, LocalDateTime startHour, LocalDateTime endHour, BookingStatus status) {
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Field not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        booking.setField(field);
        booking.setUser(user);
        booking.setMsisdn(msisdn);
        booking.setStartHour(startHour);
        booking.setEndHour(endHour);
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    @Override
    public void delete(UUID id) {
        if (!bookingRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found");
        }
        bookingRepository.deleteById(id);
    }
}
