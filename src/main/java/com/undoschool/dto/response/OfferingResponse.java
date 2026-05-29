package com.undoschool.dto.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class OfferingResponse {
    private UUID id;
    private String courseTitle;
    private String offeringTitle;
    private UUID courseId;
    private UUID teacherId;
    private String teacherTimezone;
    private List<SessionResponse> sessions;
    private OffsetDateTime createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }
    public String getOfferingTitle() { return offeringTitle; }
    public void setOfferingTitle(String offeringTitle) { this.offeringTitle = offeringTitle; }
    public UUID getCourseId() { return courseId; }
    public void setCourseId(UUID courseId) { this.courseId = courseId; }
    public UUID getTeacherId() { return teacherId; }
    public void setTeacherId(UUID teacherId) { this.teacherId = teacherId; }
    public String getTeacherTimezone() { return teacherTimezone; }
    public void setTeacherTimezone(String teacherTimezone) { this.teacherTimezone = teacherTimezone; }
    public List<SessionResponse> getSessions() { return sessions; }
    public void setSessions(List<SessionResponse> sessions) { this.sessions = sessions; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
