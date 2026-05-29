package com.undoschool.dto.response;

import java.util.UUID;

public class SessionResponse {
    private UUID id;
    private String startTime;
    private String endTime;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}
