package com.undoschool.dto.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class BookingResponse {
    private UUID id;
    private UUID offeringId;
    private String courseTitle;
    private String offeringTitle;
    private String teacherTimezone;
    private List<SessionResponse> sessions;
    private OffsetDateTime bookedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getOfferingId() { return offeringId; }
    public void setOfferingId(UUID offeringId) { this.offeringId = offeringId; }
    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }
    public String getOfferingTitle() { return offeringTitle; }
    public void setOfferingTitle(String offeringTitle) { this.offeringTitle = offeringTitle; }
    public String getTeacherTimezone() { return teacherTimezone; }
    public void setTeacherTimezone(String teacherTimezone) { this.teacherTimezone = teacherTimezone; }
    public List<SessionResponse> getSessions() { return sessions; }
    public void setSessions(List<SessionResponse> sessions) { this.sessions = sessions; }
    public OffsetDateTime getBookedAt() { return bookedAt; }
    public void setBookedAt(OffsetDateTime bookedAt) { this.bookedAt = bookedAt; }
}
