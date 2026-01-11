package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.exception.ResourceNotFoundException;
import com.example.bookingbadminton.model.Enum.BookingStatus;
import com.example.bookingbadminton.model.Enum.InvoiceStatus;
import com.example.bookingbadminton.model.entity.*;
import com.example.bookingbadminton.payload.FieldOwnerBookingListResponse;
import com.example.bookingbadminton.payload.FieldOwnerDailyBookingResponse;
import com.example.bookingbadminton.payload.PaidBookingDetailResponse;
import com.example.bookingbadminton.payload.TempBookingRequest;
import com.example.bookingbadminton.payload.TempBookingResponse;
import com.example.bookingbadminton.payload.UserBookingDetailResponse;
import com.example.bookingbadminton.payload.UserBookingSummaryResponse;
import com.example.bookingbadminton.payload.request.ValidOwnerAndFieldRequest;
import com.example.bookingbadminton.repository.*;
import com.example.bookingbadminton.service.BookingService;
import com.example.bookingbadminton.util.UploadFileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
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
    private final UploadFileUtil uploadFileUtil;

    @Override
    public void approveBooking(UUID bookingId, ValidOwnerAndFieldRequest request) {
        Owner owner = ownerRepository.findById(request.ownerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chủ quản lý."));
        Field field = fieldRepository.findByIdAndOwner(request.subFieldId(), owner)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sân không thuộc quyền quản lý."));
        Booking booking = bookingRepository.findById(bookingId)
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
        Booking booking = bookingRepository.findById(bookingId)
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
        booking.setUser(user);
        booking.setField(field);
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
                parent.getStartTime(),
                parent.getEndTime(),
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
        booking.setUser(user);
        booking.setMsisdn(user.getAccount() != null ? user.getAccount().getMsisdn() : null);
        booking.setField(parent);


        List<TempBookingResponse.TempBookingItem> items = new ArrayList<>();
        int total = 0;
        List<BookingField> links = new ArrayList<>();

        for (TempBookingRequest.TempBookingItem item : request.listBookingField()) {
            Field sub = fieldRepository.findById(item.subFieldId()).orElseThrow(() -> new ResourceNotFoundException("Không tìm thâấy sân này"));
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

        // Lưu booking trước để nhận id, sau đó lưu các BookingField gắn với booking đó
        booking.setTotalAmount(BigDecimal.valueOf(total));
        booking = bookingRepository.save(booking);
        Booking finalBooking = booking;
        links.forEach(link -> link.setBooking(finalBooking));
        List<BookingField> persistedLinks = bookingFieldRepository.saveAll(links);
        booking.setBookingField(persistedLinks);

        return new TempBookingResponse(
                parent.getId(),
                booking.getUser() != null ? booking.getUser().getId() : null,
                BookingStatus.PENDING,
                total,
                items,
                user.getName(),
                user.getAccount().getMsisdn(),
                parent.getName(),
                parent.getAddress(),
                parent.getImgQr(),
                booking.getId()
        );
    }

    @Override
    public String paying(UUID bookingId, MultipartFile file) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin đặt sân"));

        booking.setStatus(BookingStatus.COMFIRM);
        Invoice invoice = new Invoice();
        invoice.setBooking(booking);
        invoice.setStatus(InvoiceStatus.PAY);

        String imgSecure = uploadFileUtil.uploadFile(file);
        invoice.setImgPayment(imgSecure);
        invoice.setPrice(booking.getTotalAmount());
        invoiceRepository.save(invoice);
        bookingRepository.save(booking);
        return "Thanh toán thành công";
    }


    @Override
    public List<FieldOwnerBookingListResponse> ownerListBookings(UUID parentFieldId, LocalDate date) {
        Field parent = fieldRepository.findById(parentFieldId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kh?ng t?m th?y s?n!"));
        if (parent.getParentField() != null) {
            parent = parent.getParentField();
        }
        LocalDateTime now = LocalDateTime.now();
        var startOfDay = date.atStartOfDay();
        var endOfDay = date.plusDays(1).atStartOfDay();
        List<BookingField> bookingFields = bookingFieldRepository.findByParentFieldAndDay(parent.getId(), startOfDay, endOfDay);

        Map<UUID, List<BookingField>> byBooking = bookingFields.stream()
                .filter(bf -> bf.getField() == null || bf.getField().getDeletedAt() == null)
                .filter(bf -> bf.getBooking() != null)
                .filter(bf -> bf.getBooking().getDeletedAt() == null)
                .collect(Collectors.groupingBy(bf -> bf.getBooking().getId()));

        List<FieldOwnerBookingListResponse> responses = new ArrayList<>();
        for (Map.Entry<UUID, List<BookingField>> entry : byBooking.entrySet()) {
            Booking booking = entry.getValue().get(0).getBooking();
            if (BookingStatus.PENDING.equals(booking.getStatus())
                    && booking.getCreatedAt() != null
                    && booking.getCreatedAt().isBefore(now.minusMinutes(5))) {
                continue;
            }
            Invoice invoice = invoiceRepository.findByBooking(booking).orElse(null);
            BookingField anyField = entry.getValue().get(0);

            responses.add(new FieldOwnerBookingListResponse(
                    booking.getId(),
                    anyField.getField() != null ? anyField.getField().getId() : null,
                    booking.getUser() != null ? booking.getUser().getName() : null,
                    booking.getMsisdn(),
                    booking.getStatus(),
                    booking.getCreatedAt(),
                    invoice != null ? invoice.getStatus() : null
            ));
        }


        responses.sort(Comparator.comparing(FieldOwnerBookingListResponse::createdAt,
                Comparator.nullsLast(LocalDateTime::compareTo)).reversed());
        return responses;
    }

    @Override
    public List<UserBookingSummaryResponse> userBookings(UUID userId) {
        List<Booking> bookings = bookingRepository.findByUser_IdAndDeletedAtIsNullOrderByCreatedAtDesc(userId);
        List<UserBookingSummaryResponse> responses = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getBookingField() == null || booking.getBookingField().isEmpty()) {
                continue;
            }
            LocalDate date = booking.getBookingField().get(0).getStartHour().toLocalDate();
            LocalDateTime minStart = booking.getBookingField().stream()
                    .map(BookingField::getStartHour)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);
            LocalDateTime maxEnd = booking.getBookingField().stream()
                    .map(BookingField::getEndHour)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
            String timeRange = "";
            if (minStart != null && maxEnd != null) {
                timeRange = String.format("%02d:%02d - %02d:%02d",
                        minStart.getHour(), minStart.getMinute(),
                        maxEnd.getHour(), maxEnd.getMinute());
            }
            responses.add(new UserBookingSummaryResponse(
                    booking.getId(),
                    booking.getField() != null ? booking.getField().getName() : null,
                    date,
                    timeRange,
                    booking.getStatus()
            ));
        }
        return responses;
    }

    @Override
    public UserBookingDetailResponse userBookingDetail(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kh?ng t?m th?y ??n ??t s?n!"));
        if (booking.getBookingField() == null || booking.getBookingField().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "??n ??t s?n kh?ng c? l?ch ??t.");
        }
        LocalDate date = booking.getBookingField().get(0).getStartHour().toLocalDate();
        LocalDateTime minStart = booking.getBookingField().stream()
                .map(BookingField::getStartHour)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        LocalDateTime maxEnd = booking.getBookingField().stream()
                .map(BookingField::getEndHour)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        String timeRange = "";
        if (minStart != null && maxEnd != null) {
            timeRange = String.format("%02d:%02d - %02d:%02d",
                    minStart.getHour(), minStart.getMinute(),
                    maxEnd.getHour(), maxEnd.getMinute());
        }
        Field field = booking.getField();
        return new UserBookingDetailResponse(
                booking.getId(),
                field != null ? field.getName() : null,
                field != null ? field.getAddress() : null,
                field != null ? field.getMobileContact() : null,
                date,
                timeRange,
                booking.getStatus()
        );
    }

}
