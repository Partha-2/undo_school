package com.undoschool.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class AddSessionsRequest {

    @NotBlank
    private String timezone;

    @NotEmpty @Valid
    private List<CreateOfferingRequest.SessionTime> sessions;

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    public List<CreateOfferingRequest.SessionTime> getSessions() { return sessions; }
    public void setSessions(List<CreateOfferingRequest.SessionTime> sessions) { this.sessions = sessions; }
}
