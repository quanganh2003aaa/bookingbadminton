package com.example.bookingbadminton.service.impl;

import com.example.bookingbadminton.model.Enum.ActiveStatus;
import com.example.bookingbadminton.model.entity.Field;
import com.example.bookingbadminton.model.entity.FieldImage;
import com.example.bookingbadminton.model.entity.Owner;
import com.example.bookingbadminton.model.entity.TimeSlot;
import com.example.bookingbadminton.payload.FieldAdminResponse;
import com.example.bookingbadminton.payload.FieldCardResponse;
import com.example.bookingbadminton.payload.FieldDetailResponse;
import com.example.bookingbadminton.payload.FieldOwnerBookingSummary;
import com.example.bookingbadminton.payload.FieldOwnerDailyBookingResponse;
import com.example.bookingbadminton.payload.FieldOwnerDetailResponse;
import com.example.bookingbadminton.payload.FieldOwnerSummaryResponse;
import com.example.bookingbadminton.payload.FieldRequest;
import com.example.bookingbadminton.payload.FieldUserDetailResponse;
import com.example.bookingbadminton.repository.BookingFieldRepository;
import com.example.bookingbadminton.repository.BookingRepository;
import com.example.bookingbadminton.repository.CommentRepository;
import com.example.bookingbadminton.repository.FieldImageRepository;
import com.example.bookingbadminton.repository.FieldRepository;
import com.example.bookingbadminton.repository.OwnerRepository;
import com.example.bookingbadminton.repository.TimeSlotRepository;
import com.example.bookingbadminton.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FieldServiceImpl implements FieldService {

    private final FieldRepository fieldRepository;
    private final OwnerRepository ownerRepository;
    private final FieldImageRepository fieldImageRepository;
    private final CommentRepository commentRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final BookingFieldRepository bookingFieldRepository;
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
        if (field.getIndexField() == null) {
            field.setIndexField(0);
        }
        field.setMsisdn(request.msisdn());
        field.setMobileContact(request.mobileContact());
        field.setStartTime(request.startTime());
        field.setEndTime(request.endTime());
        field.setActive(request.active());
        field.setLinkMap(request.linkMap());
        Field saved = fieldRepository.save(field);
        ensureTimeSlotsAlign(saved);
        if (field.getParentField() == null && request.quantity() != null) {
            syncSubFields(saved, request.quantity());
        }
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
        List<Field> parents = page.getContent().stream()
                .filter(f -> f.getParentField() == null)
                .toList();
        Page<Field> filtered = new PageImpl<>(parents, pageable, parents.size());
        return filtered.map(this::toCardResponse);
    }

    @Override
    public Page<FieldAdminResponse> adminList(String search, Pageable pageable) {
        Page<Field> page = fieldRepository.findByFilters(search, pageable);
        List<Field> parents = page.getContent().stream()
                .filter(f -> f.getParentField() == null)
                .toList();
        Page<Field> filtered = new PageImpl<>(parents, pageable, parents.size());
        return filtered.map(f -> new FieldAdminResponse(
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
        Page<Field> page = fieldRepository.findByOwner_IdAndParentFieldIsNull(owner.getId(), pageable);
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
        if (saved.getParentField() == null && request.quantity() != null) {
            syncSubFields(saved, request.quantity());
        }
        return saved;
    }

    @Override
    public Page<FieldOwnerBookingSummary> ownerFieldBookings(UUID ownerId, Pageable pageable) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found"));
        Page<Field> page = fieldRepository.findByOwner_IdAndParentFieldIsNull(owner.getId(), pageable);
        LocalDate today = LocalDate.now();
        var counts = bookingRepository.countByParentFieldInDay(
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );
        Map<UUID, Long> countMap = new HashMap<>();
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
    public FieldOwnerDailyBookingResponse ownerDailyBookings(UUID ownerId, UUID fieldId, LocalDate date) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found"));
        Field parent = get(fieldId);
        if (!parent.getOwner().getId().equals(owner.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Field not owned by this account");
        }
        if (parent.getParentField() != null) {
            parent = parent.getParentField();
        }

        var startOfDay = date.atStartOfDay();
        var endOfDay = date.plusDays(1).atStartOfDay();
        var bookings = bookingFieldRepository.findByParentFieldAndDay(parent.getId(), startOfDay, endOfDay);

        Map<UUID, List<FieldOwnerDailyBookingResponse.BookingSlot>> slotsByField = new HashMap<>();
        for (var bf : bookings) {
            var slot = new FieldOwnerDailyBookingResponse.BookingSlot(
                    bf.getBooking() != null ? bf.getBooking().getId() : null,
                    bf.getStartHour(),
                    bf.getEndHour(),
                    bf.getBooking() != null ? bf.getBooking().getMsisdn() : null,
                    bf.getBooking() != null ? bf.getBooking().getStatus() : null
            );
            slotsByField.computeIfAbsent(bf.getField().getId(), k -> new ArrayList<>()).add(slot);
        }

        List<Field> children = parent.getSubFields();
        if (children == null || children.isEmpty()) {
            children = List.of(parent);
        }
        children = children.stream()
                .sorted(Comparator.comparing(f -> f.getIndexField() == null ? Integer.MAX_VALUE : f.getIndexField()))
                .toList();

        var subFieldBookings = children.stream()
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
                subFieldBookings
        );
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
        if (existing.isEmpty()) {
            TimeSlot slot = new TimeSlot();
            slot.setField(field);
            slot.setPrice(0);
            slot.setStartHour(field.getStartTime());
            slot.setEndHour(field.getEndTime());
            timeSlotRepository.save(slot);
            return;
        }

        List<TimeSlot> processed = new java.util.ArrayList<>();
        LocalTime expectedStart = field.getStartTime();

        for (TimeSlot s : existing) {
            if (s.getStartHour() == null || s.getEndHour() == null || !s.getStartHour().isBefore(s.getEndHour())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid timeslot range");
            }
            if (!s.getEndHour().isAfter(field.getStartTime())) {
                continue;
            }
            LocalTime start = s.getStartHour().isBefore(expectedStart) ? expectedStart : s.getStartHour();
            LocalTime end = s.getEndHour().isAfter(field.getEndTime()) ? field.getEndTime() : s.getEndHour();
            if (!start.isBefore(end)) {
                continue;
            }
            if (!start.equals(expectedStart)) {
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

        timeSlotRepository.deleteAll(existing);
        timeSlotRepository.saveAll(processed);
    }

    private void syncSubFields(Field parent, int newQuantity) {
        // Đồng bộ thông tin chung cho sân cha
        parent.setIndexField(0);
        fieldRepository.save(parent);

        List<Field> subs = fieldRepository.findByParentField_IdOrderByIndexFieldAsc(parent.getId());
        int currentActive = (int) subs.stream().filter(s -> s.getDeletedAt() == null).count();
        int maxIndex = subs.stream()
                .map(Field::getIndexField)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(0);

        // Cập nhật dữ liệu chung cho các sân con còn hoạt động
        subs.stream()
                .filter(s -> s.getDeletedAt() == null)
                .forEach(sf -> {
                    copyCommonFields(sf, parent);
                    fieldRepository.save(sf);
                });

        if (newQuantity < currentActive) {
            int toRemove = currentActive - newQuantity;
            LocalDateTime now = LocalDateTime.now();
            var removable = subs.stream()
                    .filter(s -> s.getDeletedAt() == null)
                    .sorted(Comparator.comparing(Field::getIndexField, Comparator.nullsLast(Integer::compareTo)).reversed())
                    .toList();
            for (int i = 0; i < toRemove && i < removable.size(); i++) {
                Field sf = removable.get(i);
                sf.setDeletedAt(now);
                fieldRepository.save(sf);
            }
            return;
        }

        if (newQuantity > currentActive) {
            int need = newQuantity - currentActive;
            // Tái sử dụng sân con đã xóa nếu có
            var reusable = subs.stream()
                    .filter(s -> s.getDeletedAt() != null)
                    .sorted(Comparator.comparing(Field::getIndexField, Comparator.nullsLast(Integer::compareTo)))
                    .toList();
            for (Field sf : reusable) {
                if (need <= 0) break;
                sf.setDeletedAt(null);
                if (sf.getIndexField() == null) {
                    sf.setIndexField(++maxIndex);
                }
                copyCommonFields(sf, parent);
                fieldRepository.save(sf);
                need--;
            }
            while (need > 0) {
                Field sf = new Field();
                sf.setParentField(parent);
                sf.setIndexField(++maxIndex);
                copyCommonFields(sf, parent);
                sf.setQuantity(0);
                fieldRepository.save(sf);
                need--;
            }
        }
    }

    private void copyCommonFields(Field target, Field parent) {
        target.setOwner(parent.getOwner());
        target.setAddress(parent.getAddress());
        target.setMsisdn(parent.getMsisdn());
        target.setMobileContact(parent.getMobileContact());
        target.setStartTime(parent.getStartTime());
        target.setEndTime(parent.getEndTime());
        target.setActive(parent.getActive());
        target.setLinkMap(parent.getLinkMap());
        if (target.getIndexField() != null && target.getIndexField() > 0) {
            target.setName(parent.getName() + " - Sân " + target.getIndexField());
            target.setQuantity(0);
        } else {
            target.setName(parent.getName());
            target.setQuantity(parent.getQuantity());
        }
    }
}
