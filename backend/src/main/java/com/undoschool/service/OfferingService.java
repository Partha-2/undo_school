package com.undoschool.service;

import com.undoschool.dto.request.AddSessionsRequest;
import com.undoschool.dto.request.CreateOfferingRequest;
import com.undoschool.dto.response.OfferingResponse;
import com.undoschool.dto.response.SessionResponse;
import com.undoschool.entity.Course;
import com.undoschool.entity.Offering;
import com.undoschool.entity.Session;
import com.undoschool.entity.Teacher;
import com.undoschool.exception.NotFoundException;
import com.undoschool.repository.CourseRepository;
import com.undoschool.repository.OfferingRepository;
import com.undoschool.repository.SessionRepository;
import com.undoschool.repository.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OfferingService {

    private final CourseRepository courseRepo;
    private final TeacherRepository teacherRepo;
    private final OfferingRepository offeringRepo;
    private final SessionRepository sessionRepo;
    private final TimezoneService tzService;

    public OfferingService(CourseRepository courseRepo, TeacherRepository teacherRepo,
                           OfferingRepository offeringRepo, SessionRepository sessionRepo,
                           TimezoneService tzService) {
        this.courseRepo = courseRepo;
        this.teacherRepo = teacherRepo;
        this.offeringRepo = offeringRepo;
        this.sessionRepo = sessionRepo;
        this.tzService = tzService;
    }

    @Transactional
    public Offering create(UUID teacherId, CreateOfferingRequest req) {
        Course course = courseRepo.findById(req.getCourseId())
                .orElseThrow(() -> new NotFoundException("Course not found: " + req.getCourseId()));
        Teacher teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new NotFoundException("Teacher not found: " + teacherId));

        tzService.validate(req.getTimezone());

        List<Session> sessions = new ArrayList<>();
        for (CreateOfferingRequest.SessionTime s : req.getSessions()) {
            OffsetDateTime start = tzService.localToUtc(s.getStartTime(), req.getTimezone());
            OffsetDateTime end = tzService.localToUtc(s.getEndTime(), req.getTimezone());
            if (!end.isAfter(start)) {
                throw new IllegalArgumentException("Session end must be after start: " + s.getStartTime() + " - " + s.getEndTime());
            }
            Session session = new Session();
            session.setTeacherId(teacherId);
            session.setStartTime(start);
            session.setEndTime(end);
            sessions.add(session);
        }

        Offering offering = new Offering();
        offering.setCourseId(req.getCourseId());
        offering.setTeacherId(teacherId);
        offering.setTitle(req.getTitle());
        offering.setTimezone(req.getTimezone());

        offering = offeringRepo.save(offering);

        for (Session s : sessions) {
            s.setOfferingId(offering.getId());
            s.setOffering(offering);
        }
        sessionRepo.saveAll(sessions);
        offering.setSessions(sessions);

        return offering;
    }

    @Transactional
    public Offering addSessions(UUID teacherId, UUID offeringId, AddSessionsRequest req) {
        Offering offering = offeringRepo.findById(offeringId)
                .orElseThrow(() -> new NotFoundException("Offering not found: " + offeringId));
        if (!offering.getTeacherId().equals(teacherId)) {
            throw new NotFoundException("Offering not found or not owned by this teacher");
        }

        tzService.validate(req.getTimezone());

        List<Session> newSessions = new ArrayList<>();
        for (CreateOfferingRequest.SessionTime s : req.getSessions()) {
            OffsetDateTime start = tzService.localToUtc(s.getStartTime(), req.getTimezone());
            OffsetDateTime end = tzService.localToUtc(s.getEndTime(), req.getTimezone());
            if (!end.isAfter(start)) {
                throw new IllegalArgumentException("Session end must be after start");
            }
            Session session = new Session();
            session.setOfferingId(offeringId);
            session.setTeacherId(teacherId);
            session.setStartTime(start);
            session.setEndTime(end);
            newSessions.add(session);
        }
        sessionRepo.saveAll(newSessions);
        return offeringRepo.findById(offeringId)
                .orElseThrow(() -> new NotFoundException("Offering not found"));
    }

    public List<OfferingResponse> getTeacherOfferings(UUID teacherId, String timezone) {
        List<Offering> offerings = offeringRepo.findByTeacherIdOrderByCreatedAtDesc(teacherId);
        return toResponseList(offerings, timezone);
    }

    public List<OfferingResponse> getAllOfferings(String timezone) {
        List<Offering> offerings = offeringRepo.findAll();
        return toResponseList(offerings, timezone);
    }

    private List<OfferingResponse> toResponseList(List<Offering> offerings, String tz) {
        return offerings.stream().map(o -> {
            OfferingResponse r = new OfferingResponse();
            r.setId(o.getId());
            r.setCourseTitle(o.getCourse() != null ? o.getCourse().getTitle() : null);
            r.setOfferingTitle(o.getTitle());
            r.setCourseId(o.getCourseId());
            r.setTeacherId(o.getTeacherId());
            r.setTeacherTimezone(o.getTimezone());
            r.setCreatedAt(o.getCreatedAt());

            List<SessionResponse> sessionResponses = new ArrayList<>();
            if (o.getSessions() != null) {
                for (Session s : o.getSessions()) {
                    SessionResponse sr = new SessionResponse();
                    sr.setId(s.getId());
                    if (tz != null && !tz.isBlank()) {
                        sr.setStartTime(tzService.utcToTimezoneWithOffset(s.getStartTime(), tz));
                        sr.setEndTime(tzService.utcToTimezoneWithOffset(s.getEndTime(), tz));
                    } else {
                        sr.setStartTime(s.getStartTime().toString());
                        sr.setEndTime(s.getEndTime().toString());
                    }
                    sessionResponses.add(sr);
                }
            }
            r.setSessions(sessionResponses);
            return r;
        }).toList();
    }
}
