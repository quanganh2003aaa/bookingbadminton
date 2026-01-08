package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.Enum.BookingStatus;
import com.example.bookingbadminton.model.Enum.InvoiceStatus;
import com.example.bookingbadminton.model.entity.*;
import com.example.bookingbadminton.payload.FieldOwnerDailyBookingResponse;
import com.example.bookingbadminton.payload.TempBookingRequest;
import com.example.bookingbadminton.payload.TempBookingResponse;
import com.example.bookingbadminton.payload.request.ValidOwnerAndFieldRequest;
import com.example.bookingbadminton.repository.*;
import com.example.bookingbadminton.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final FieldRepository fieldRepository;
    private final UserRepository userRepository;
    private final BookingFieldRepository bookingFieldRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final OwnerRepository ownerRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    public void approveBooking(UUID bookingId, ValidOwnerAndFieldRequest request) {
        Owner owner = ownerRepository.findById(request.ownerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chủ quản lý."));
        Field field = fieldRepository.findByIdAndOwner(request.subFieldId(), owner)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sân không thuộc quyền quản lý."));
        Booking booking = bookingRepository.findByIdAndField(bookingId, field)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Đơn đặt sân không xác định."));

        booking.setStatus(BookingStatus.ACCEPT);
        bookingRepository.save(booking);
    }

    @Override
    public void rejectBooking(UUID bookingId, ValidOwnerAndFieldRequest request) {
        Owner owner = ownerRepository.findById(request.ownerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chủ quản lý."));
        Field field = fieldRepository.findByIdAndOwner(request.subFieldId(), owner)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sân không thuộc quyền quản lý."));
        Booking booking = bookingRepository.findByIdAndField(bookingId, field)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Đơn đặt sân không xác định."));

        booking.setStatus(BookingStatus.INACCEPT);
        bookingRepository.save(booking);
        Invoice invoice = invoiceRepository.findByBooking(booking)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice không xác định."));
        invoice.setStatus(InvoiceStatus.REFUND);
        invoiceRepository.save(invoice);
    }

    @Override
    public Booking get(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
    }

    private Booking saveBooking(Booking booking, UUID fieldId, UUID userId, String msisdn, Integer indexField, LocalDateTime startHour, LocalDateTime endHour, BookingStatus status) {
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Field not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        booking.setField(field);
        booking.setUser(user);
        booking.setMsisdn(msisdn);
        booking.setStatus(status);
        syncBookingField(booking, field, startHour, endHour);
        return bookingRepository.save(booking);
    }

    private void syncBookingField(Booking booking, Field field, LocalDateTime startHour, LocalDateTime endHour) {
        List<BookingField> links = booking.getBookingField();
        if (links == null || links.isEmpty()) {
            BookingField link = new BookingField();
            link.setBooking(booking);
            link.setField(field);
            link.setStartHour(startHour);
            link.setEndHour(endHour);
            link.setIndexField(field.getIndexField());
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
        link.setIndexField(field.getIndexField());
    }

    @Override
    public FieldOwnerDailyBookingResponse bookingsByDayForField(UUID fieldId, LocalDate date) {
        Field parent = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sân!"));
        if (parent.getParentField() != null) {
            parent = parent.getParentField();
        }
        var startOfDay = date.atStartOfDay();
        var endOfDay = date.plusDays(1).atStartOfDay();
        var bookingFields = bookingFieldRepository.findByParentFieldAndDay(parent.getId(), startOfDay, endOfDay);

        Map<UUID, List<FieldOwnerDailyBookingResponse.BookingSlot>> slotsByField = new HashMap<>();
        for (var bf : bookingFields) {
            if (bf.getField() != null && bf.getField().getDeletedAt() != null) {
                continue;
            }
            var slot = new FieldOwnerDailyBookingResponse.BookingSlot(
                    bf.getBooking() != null ? bf.getBooking().getId() : null,
                    bf.getStartHour(),
                    bf.getEndHour(),
                    bf.getBooking() != null ? bf.getBooking().getStatus() : null
            );
            slotsByField.computeIfAbsent(bf.getField().getId(), k -> new ArrayList<>()).add(slot);
        }

        List<Field> children = parent.getSubFields();
        if (children == null || children.isEmpty()) {
            children = List.of(parent);
        }
        children = children.stream()
                .filter(f -> f.getDeletedAt() == null)
                .sorted(Comparator.comparing(f -> f.getIndexField() == null ? Integer.MAX_VALUE : f.getIndexField()))
                .collect(Collectors.toList());

        var subFields = children.stream()
                .map(f -> new FieldOwnerDailyBookingResponse.SubFieldBooking(
                        f.getId(),
                        f.getIndexField(),
                        slotsByField.getOrDefault(f.getId(), List.of())
                ))
                .toList();

        return new FieldOwnerDailyBookingResponse(
                parent.getId(),
                parent.getName(),
                date,
                subFields
        );
    }

    @Override
    @Transactional
    public TempBookingResponse createTempPendingBooking(TempBookingRequest request) {
        Field parent = fieldRepository.findById(request.parentFieldId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sân cha!"));

        // Lọc sân con còn hiệu lực
        List<Field> children = parent.getSubFields();
        if (children == null || children.isEmpty()) {
            children = List.of(parent);
        }
        children = children.stream()
                .filter(f -> f.getDeletedAt() == null)
                .collect(Collectors.toList());
        Map<UUID, Field> childMap = children.stream()
                .collect(Collectors.toMap(Field::getId, f -> f));

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng!"));

        // Giá dựa trên slot của sân cha
        var parentSlots = timeSlotRepository.findByField_IdOrderByStartHour(parent.getId());
        LocalDateTime now = LocalDateTime.now();

        // Lấy booking trong ngày để kiểm tra trùng và dọn pending quá hạn
        Map<LocalDate, List<BookingField>> bookingFieldByDate = request.listBookingField().stream()
                .map(TempBookingRequest.TempBookingItem::date)
                .distinct()
                .collect(Collectors.toMap(
                        d -> d,
                        d -> bookingFieldRepository.findByParentFieldAndDay(parent.getId(), d.atStartOfDay(), d.plusDays(1).atStartOfDay())
                ));

        bookingFieldByDate.values().forEach(list -> list.forEach(bf -> {
            Booking b = bf.getBooking();
            if (b == null || b.getDeletedAt() != null) {
                return;
            }
            if (BookingStatus.PENDING.equals(b.getStatus()) && b.getCreatedAt() != null
                    && b.getCreatedAt().isBefore(now.minusMinutes(5))) {
                b.setDeletedAt(now);
                bookingRepository.save(b);
            }
        }));

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.PENDING);
        booking.setField(parent);
        booking.setUser(user);
        booking.setMsisdn(user.getAccount() != null ? user.getAccount().getMsisdn() : null);

        List<TempBookingResponse.TempBookingItem> items = new ArrayList<>();
        int total = 0;
        List<BookingField> links = new ArrayList<>();

        for (TempBookingRequest.TempBookingItem item : request.listBookingField()) {
            Field sub = childMap.get(item.subFieldId());
            if (sub == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sân con không thuộc sân cha hoặc đã bị xóa!");
            }

            LocalDateTime start = item.date().atTime(item.startHour());
            LocalDateTime end = item.date().atTime(item.endHour());

            // Kiểm tra trùng lịch với các đơn đã đặt/đang pending còn hiệu lực
            List<BookingField> existingInDay = bookingFieldByDate.getOrDefault(item.date(), List.of());
            for (BookingField existed : existingInDay) {
                Booking existedBooking = existed.getBooking();
                if (existedBooking == null || existedBooking.getDeletedAt() != null) {
                    continue;
                }
                if (BookingStatus.PENDING.equals(existedBooking.getStatus())
                        && existedBooking.getCreatedAt() != null
                        && existedBooking.getCreatedAt().isBefore(now.minusMinutes(5))) {
                    continue;
                }
                if (!(BookingStatus.ACCEPT.equals(existedBooking.getStatus())
                        || BookingStatus.INACCEPT.equals(existedBooking.getStatus())
                        || BookingStatus.PENDING.equals(existedBooking.getStatus()))) {
                    continue;
                }
                if (!existed.getField().getId().equals(sub.getId())) {
                    continue;
                }
                if (start.isBefore(existed.getEndHour()) && end.isAfter(existed.getStartHour())) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Khung giờ này đã được đặt. Vui lòng chọn thời gian khác.");
                }
            }

            // Tính giá theo slot của sân cha (dựa trên giờ bắt đầu)
            int price = parentSlots.stream()
                    .filter(ts -> !ts.getStartHour().isAfter(item.startHour()) && ts.getEndHour().isAfter(item.startHour()))
                    .map(ts -> ts.getPrice() == null ? 0 : ts.getPrice())
                    .findFirst()
                    .orElse(0);
            total += price;

            BookingField bf = new BookingField();
            bf.setBooking(booking);
            bf.setField(sub);
            bf.setStartHour(start);
            bf.setEndHour(end);
            bf.setIndexField(sub.getIndexField());
            links.add(bf);

            items.add(new TempBookingResponse.TempBookingItem(
                    sub.getId(),
                    sub.getIndexField(),
                    item.date(),
                    item.startHour(),
                    item.endHour(),
                    price
            ));
        }

        booking.setBookingField(links);
        bookingRepository.save(booking);

        return new TempBookingResponse(
                parent.getId(),
                booking.getUser() != null ? booking.getUser().getId() : null,
                BookingStatus.PENDING,
                total,
                items
        );
    }
}
