package com.undoschool.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class CreateOfferingRequest {

    @NotNull
    private UUID courseId;

    @NotBlank
    private String title;

    @NotBlank
    private String timezone;

    @NotEmpty @Valid
    private List<SessionTime> sessions;

    public UUID getCourseId() { return courseId; }
    public void setCourseId(UUID courseId) { this.courseId = courseId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    public List<SessionTime> getSessions() { return sessions; }
    public void setSessions(List<SessionTime> sessions) { this.sessions = sessions; }

    public static class SessionTime {
        @NotBlank
        private String startTime;
        @NotBlank
        private String endTime;

        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
    }
}
