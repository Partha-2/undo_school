package com.undoschool.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class BookOfferingRequest {
    @NotNull
    private UUID offeringId;

    public UUID getOfferingId() { return offeringId; }
    public void setOfferingId(UUID offeringId) { this.offeringId = offeringId; }
}
