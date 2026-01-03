package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.Enum.ActiveStatus;
import com.example.bookingbadminton.model.entity.Field;
import com.example.bookingbadminton.model.entity.FieldImage;
import com.example.bookingbadminton.model.entity.Owner;
import com.example.bookingbadminton.model.entity.TimeSlot;
import com.example.bookingbadminton.payload.FieldAdminResponse;
import com.example.bookingbadminton.payload.FieldCardResponse;
import com.example.bookingbadminton.payload.FieldRequest;
import com.example.bookingbadminton.payload.FieldDetailResponse;
import com.example.bookingbadminton.payload.FieldOwnerDetailResponse;
import com.example.bookingbadminton.payload.FieldOwnerSummaryResponse;
import com.example.bookingbadminton.payload.FieldOwnerBookingSummary;
import com.example.bookingbadminton.payload.FieldUserDetailResponse;
import com.example.bookingbadminton.repository.AccountRepository;
import com.example.bookingbadminton.repository.FieldRepository;
import com.example.bookingbadminton.repository.FieldImageRepository;
import com.example.bookingbadminton.repository.OwnerRepository;
import com.example.bookingbadminton.repository.CommentRepository;
import com.example.bookingbadminton.repository.TimeSlotRepository;
import com.example.bookingbadminton.repository.BookingRepository;
import com.example.bookingbadminton.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FieldServiceImpl implements FieldService {

    private final FieldRepository fieldRepository;
    private final AccountRepository accountRepository;
    private final OwnerRepository ownerRepository;
    private final FieldImageRepository fieldImageRepository;
    private final CommentRepository commentRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<Field> findAll() {
        return fieldRepository.findAll();
    }

    @Override
    public Field get(UUID id) {
        return fieldRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Field not found"));
    }

    @Override
    public Field create(FieldRequest request) {
        return saveField(new Field(), request);
    }

    @Override
    public Field update(UUID id, FieldRequest request) {
        return saveField(get(id), request);
    }

    private Field saveField(Field field, FieldRequest request) {
        Owner owner = ownerRepository.findById(request.ownerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found"));
        field.setOwner(owner);
        field.setName(request.name());
        field.setAddress(request.address());
        field.setQuantity(request.quantity());
        field.setMsisdn(request.msisdn());
        field.setMobileContact(request.mobileContact());
        field.setStartTime(request.startTime());
        field.setEndTime(request.endTime());
        field.setActive(request.active());
        field.setLinkMap(request.linkMap());
        Field saved = fieldRepository.save(field);
        ensureTimeSlotsAlign(saved);
        return saved;
    }

    @Override
    public void delete(UUID id) {
        Field field = get(id);
        field.setDeletedAt(LocalDateTime.now());
        fieldRepository.save(field);
    }

    @Override
    public Page<FieldCardResponse> search(String search, ActiveStatus active, Pageable pageable) {
        Page<Field> page = fieldRepository.findByFiltersForUser(search, active, pageable);
        return page.map(this::toCardResponse);
    }

    @Override
    public Page<FieldAdminResponse> adminList(String search, Pageable pageable) {
        Page<Field> page = fieldRepository.findByFilters(search, pageable);
        return page.map(f -> new FieldAdminResponse(
                f.getId(),
                f.getName(),
                f.getOwner() != null && f.getOwner().getAccount() != null ? f.getOwner().getAccount().getGmail() : null
        ));
    }

    @Override
    public FieldDetailResponse detail(UUID id) {
        Field f = get(id);
        return new FieldDetailResponse(
                f.getId(),
                f.getName(),
                f.getAddress(),
                f.getOwner() != null ? f.getOwner().getName() : null,
                f.getOwner() != null && f.getOwner().getAccount() != null ? f.getOwner().getAccount().getGmail() : null,
                f.getMsisdn(),
                f.getMobileContact(),
                f.getStartTime(),
                f.getEndTime(),
                f.getActive(),
                f.getLinkMap()
        );
    }

    @Override
    public Page<FieldOwnerSummaryResponse> ownerFields(UUID ownerId, Pageable pageable) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found"));
        Page<Field> page = fieldRepository.findByOwner_Id(owner.getId(), pageable);
        return page.map(this::toOwnerSummary);
    }

    @Override
    public FieldOwnerDetailResponse ownerFieldDetail(UUID ownerId, UUID fieldId) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found"));
        Field field = get(fieldId);
        if (!field.getOwner().getId().equals(owner.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Field not owned by this account");
        }
        var images = fieldImageRepository.findByField_Id(fieldId)
                .stream()
                .map(FieldImage::getImage)
                .toList();
        var timeSlots = timeSlotRepository.findByField_IdOrderByStartHour(fieldId)
                .stream()
                .map(ts -> new FieldOwnerDetailResponse.TimeSlotResponse(
                        ts.getId(),
                        ts.getStartHour(),
                        ts.getEndHour(),
                        ts.getPrice()
                ))
                .toList();
        return new FieldOwnerDetailResponse(
                field.getId(),
                field.getName(),
                field.getAddress(),
                field.getQuantity(),
                field.getMsisdn(),
                field.getMobileContact(),
                field.getStartTime(),
                field.getEndTime(),
                field.getActive(),
                field.getLinkMap(),
                images,
                timeSlots
        );
    }

    @Override
    public Field ownerUpdate(UUID ownerId, UUID fieldId, FieldRequest request) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found"));
        Field field = get(fieldId);
        if (!field.getOwner().getId().equals(owner.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Field not owned by this account");
        }
        field.setName(request.name());
        field.setAddress(request.address());
        field.setQuantity(request.quantity());
        field.setMsisdn(request.msisdn());
        field.setMobileContact(request.mobileContact());
        field.setStartTime(request.startTime());
        field.setEndTime(request.endTime());
        field.setActive(request.active());
        field.setLinkMap(request.linkMap());
        Field saved = fieldRepository.save(field);
        ensureTimeSlotsAlign(saved);
        return saved;
    }

    @Override
    public Page<FieldOwnerBookingSummary> ownerFieldBookings(UUID ownerId, Pageable pageable) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found"));
        Page<Field> page = fieldRepository.findByOwner_Id(owner.getId(), pageable);
        LocalDate today = LocalDate.now();
        var counts = bookingRepository.countByFieldInDay(
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );
        java.util.Map<UUID, Long> countMap = new java.util.HashMap<>();
        for (Object[] row : counts) {
            countMap.put((UUID) row[0], (Long) row[1]);
        }
        return page.map(f -> new FieldOwnerBookingSummary(
                f.getId(),
                f.getName(),
                countMap.getOrDefault(f.getId(), 0L),
                f.getActive()
        ));
    }

    @Override
    public FieldUserDetailResponse userDetail(UUID fieldId) {
        Field f = get(fieldId);
        var images = fieldImageRepository.findByField_Id(fieldId)
                .stream().map(FieldImage::getImage).toList();
        var avatar = images.isEmpty() ? null : images.get(0);
        var comments = commentRepository.findByField_IdOrderByCreatedAtDesc(fieldId)
                .stream()
                .map(c -> new FieldUserDetailResponse.FieldCommentResponse(
                        c.getUser() != null ? c.getUser().getName() : null,
                        c.getRate(),
                        c.getContent()
                ))
                .toList();
        var slots = timeSlotRepository.findByField_IdOrderByStartHour(fieldId)
                .stream()
                .map(ts -> new FieldUserDetailResponse.TimeSlotResponse(
                        ts.getId(),
                        ts.getStartHour(),
                        ts.getEndHour(),
                        ts.getPrice()
                ))
                .toList();
        return new FieldUserDetailResponse(
                f.getId(),
                f.getName(),
                f.getAddress(),
                f.getMobileContact(),
                avatar,
                f.getStartTime(),
                f.getEndTime(),
                f.getActive(),
                f.getLinkMap(),
                images,
                comments,
                slots
        );
    }

    private FieldOwnerSummaryResponse toOwnerSummary(Field field) {
        long totalComments = commentRepository.countByField_Id(field.getId());
        Double avg = commentRepository.averageRateByFieldId(field.getId());
        Float averageRate = avg == null ? null : avg.floatValue();
        return new FieldOwnerSummaryResponse(
                field.getId(),
                field.getName(),
                field.getAddress(),
                field.getQuantity(),
                averageRate,
                totalComments
        );
    }

    private FieldCardResponse toCardResponse(Field field) {
        String image = fieldImageRepository.findFirstByField_Id(field.getId())
                .map(FieldImage::getImage)
                .orElse(null);
        return new FieldCardResponse(
                field.getId(),
                field.getName(),
                field.getAddress(),
                field.getStartTime(),
                field.getEndTime(),
                field.getMobileContact(),
                image
        );
    }

    private void ensureTimeSlotsAlign(Field field) {
        if (field.getStartTime() == null || field.getEndTime() == null) {
            return;
        }
        if (!field.getStartTime().isBefore(field.getEndTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Field start time must be before end time");
        }

        var existing = timeSlotRepository.findByField_IdOrderByStartHour(field.getId());
        // Không có slot -> tạo slot bao toàn bộ
        if (existing.isEmpty()) {
            TimeSlot slot = new TimeSlot();
            slot.setField(field);
            slot.setPrice(0);
            slot.setStartHour(field.getStartTime());
            slot.setEndHour(field.getEndTime());
            timeSlotRepository.save(slot);
            return;
        }

        // Lọc và điều chỉnh timeslot theo khung giờ mới, đồng thời cắt bỏ phần thừa
        java.util.List<TimeSlot> processed = new java.util.ArrayList<>();
        LocalTime expectedStart = field.getStartTime();

        for (TimeSlot s : existing) {
            if (s.getStartHour() == null || s.getEndHour() == null || !s.getStartHour().isBefore(s.getEndHour())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid timeslot range");
            }
            // slot kết thúc trước giờ mở cửa mới -> bỏ qua
            if (!s.getEndHour().isAfter(field.getStartTime())) {
                continue;
            }
            LocalTime start = s.getStartHour().isBefore(expectedStart) ? expectedStart : s.getStartHour();
            LocalTime end = s.getEndHour().isAfter(field.getEndTime()) ? field.getEndTime() : s.getEndHour();
            if (!start.isBefore(end)) {
                continue;
            }
            if (!start.equals(expectedStart)) {
                // đảm bảo liên tục
                start = expectedStart;
            }
            TimeSlot slot = new TimeSlot();
            slot.setField(field);
            slot.setPrice(s.getPrice());
            slot.setStartHour(start);
            slot.setEndHour(end);
            processed.add(slot);
            expectedStart = end;
            if (!expectedStart.isBefore(field.getEndTime())) {
                break;
            }
        }

        // Nếu chưa phủ tới endTime, kéo dài slot cuối hoặc tạo mới
        if (processed.isEmpty()) {
            TimeSlot slot = new TimeSlot();
            slot.setField(field);
            slot.setPrice(0);
            slot.setStartHour(field.getStartTime());
            slot.setEndHour(field.getEndTime());
            processed.add(slot);
        } else {
            TimeSlot last = processed.get(processed.size() - 1);
            if (!last.getEndHour().equals(field.getEndTime())) {
                last.setEndHour(field.getEndTime());
            }
        }

        // Lưu: xóa hết slot cũ, lưu danh sách mới (toàn bộ là entity mới hoặc đã chỉnh)
        timeSlotRepository.deleteAll(existing);
        timeSlotRepository.saveAll(processed);
    }
}
