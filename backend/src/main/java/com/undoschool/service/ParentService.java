package com.undoschool.service;

import com.undoschool.dto.request.BookOfferingRequest;
import com.undoschool.dto.request.CreateParentRequest;
import com.undoschool.dto.response.BookingResponse;
import com.undoschool.dto.response.SessionResponse;
import com.undoschool.entity.*;
import com.undoschool.exception.ConflictException;
import com.undoschool.exception.NotFoundException;
import com.undoschool.repository.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.*;

@Service
public class ParentService {

    private final ParentRepository parentRepo;
    private final OfferingRepository offeringRepo;
    private final SessionRepository sessionRepo;
    private final BookingRepository bookingRepo;
    private final TimezoneService tzService;

    public ParentService(ParentRepository parentRepo, OfferingRepository offeringRepo,
                         SessionRepository sessionRepo, BookingRepository bookingRepo,
                         TimezoneService tzService) {
        this.parentRepo = parentRepo;
        this.offeringRepo = offeringRepo;
        this.sessionRepo = sessionRepo;
        this.bookingRepo = bookingRepo;
        this.tzService = tzService;
    }

    public Parent create(CreateParentRequest req) {
        if (req.getTimezone() != null && !req.getTimezone().isBlank()) {
            tzService.validate(req.getTimezone());
        }
        Parent parent = new Parent();
        parent.setName(req.getName());
        parent.setEmail(req.getEmail());
        parent.setTimezone(req.getTimezone() != null && !req.getTimezone().isBlank() ? req.getTimezone() : "UTC");
        return parentRepo.save(parent);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Booking book(UUID parentId, BookOfferingRequest req) {
        UUID offeringId = req.getOfferingId();

        parentRepo.findById(parentId)
                .orElseThrow(() -> new NotFoundException("Parent not found: " + parentId));

        offeringRepo.findById(offeringId)
                .orElseThrow(() -> new NotFoundException("Offering not found: " + offeringId));

        List<Session> newSessions = sessionRepo.findByOfferingIdWithLock(offeringId);
        if (newSessions.isEmpty()) {
            throw new IllegalArgumentException("Offering has no sessions");
        }

        Optional<Booking> existing = bookingRepo.findByParentIdAndOfferingId(parentId, offeringId);
        if (existing.isPresent()) {
            throw new ConflictException("Offering already booked by this parent");
        }

        List<Booking> parentBookings = bookingRepo.findByParentId(parentId);
        Set<UUID> bookedSessionIds = new HashSet<>();
        for (Booking b : parentBookings) {
            if (b.getOffering() != null && b.getOffering().getSessions() != null) {
                for (Session s : b.getOffering().getSessions()) {
                    bookedSessionIds.add(s.getId());
                }
            }
        }

        List<Session> bookedSessions = new ArrayList<>();
        if (!bookedSessionIds.isEmpty()) {
            bookedSessions = sessionRepo.findByIdsWithLock(new ArrayList<>(bookedSessionIds));
        }

        for (Session ns : newSessions) {
            for (Session bs : bookedSessions) {
                if (overlaps(ns.getStartTime(), ns.getEndTime(), bs.getStartTime(), bs.getEndTime())) {
                    throw new ConflictException(
                        "Session time conflict: " + ns.getStartTime() + " - " + ns.getEndTime()
                        + " overlaps with " + bs.getStartTime() + " - " + bs.getEndTime());
                }
            }
        }

        try {
            Booking booking = new Booking();
            booking.setParentId(parentId);
            booking.setOfferingId(offeringId);
            return bookingRepo.save(booking);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Already booked this offering");
        }
    }

    public List<BookingResponse> getBookings(UUID parentId, String timezone) {
        parentRepo.findById(parentId)
                .orElseThrow(() -> new NotFoundException("Parent not found: " + parentId));

        List<Booking> bookings = bookingRepo.findByParentIdOrderByCreatedAtDesc(parentId);
        return bookings.stream().map(b -> {
            BookingResponse r = new BookingResponse();
            r.setId(b.getId());
            r.setOfferingId(b.getOfferingId());
            if (b.getOffering() != null) {
                r.setCourseTitle(b.getOffering().getCourse() != null
                        ? b.getOffering().getCourse().getTitle() : null);
                r.setOfferingTitle(b.getOffering().getTitle());
                r.setTeacherTimezone(b.getOffering().getTimezone());
            }
            r.setBookedAt(b.getCreatedAt());

            List<SessionResponse> sessions = new ArrayList<>();
            if (b.getOffering() != null && b.getOffering().getSessions() != null) {
                for (Session s : b.getOffering().getSessions()) {
                    SessionResponse sr = new SessionResponse();
                    sr.setId(s.getId());
                    if (timezone != null && !timezone.isBlank()) {
                        sr.setStartTime(tzService.utcToTimezoneWithOffset(s.getStartTime(), timezone));
                        sr.setEndTime(tzService.utcToTimezoneWithOffset(s.getEndTime(), timezone));
                    } else {
                        sr.setStartTime(s.getStartTime().toString());
                        sr.setEndTime(s.getEndTime().toString());
                    }
                    sessions.add(sr);
                }
            }
            r.setSessions(sessions);
            return r;
        }).toList();
    }

    private boolean overlaps(OffsetDateTime s1, OffsetDateTime e1, OffsetDateTime s2, OffsetDateTime e2) {
        return s1.isBefore(e2) && s2.isBefore(e1);
    }
}
