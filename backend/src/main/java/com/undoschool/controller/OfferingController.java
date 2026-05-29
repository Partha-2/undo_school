package com.undoschool.controller;

import com.undoschool.dto.request.AddSessionsRequest;
import com.undoschool.dto.request.CreateOfferingRequest;
import com.undoschool.dto.response.OfferingResponse;
import com.undoschool.entity.Offering;
import com.undoschool.service.OfferingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/teachers/{teacherId}/offerings")
public class OfferingController {

    private final OfferingService offeringService;

    public OfferingController(OfferingService offeringService) {
        this.offeringService = offeringService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Offering create(@PathVariable UUID teacherId, @Valid @RequestBody CreateOfferingRequest req) {
        return offeringService.create(teacherId, req);
    }

    @PostMapping("/{offeringId}/sessions")
    public Offering addSessions(@PathVariable UUID teacherId, @PathVariable UUID offeringId,
                                @Valid @RequestBody AddSessionsRequest req) {
        return offeringService.addSessions(teacherId, offeringId, req);
    }

    @GetMapping
    public List<OfferingResponse> list(@PathVariable UUID teacherId,
                                       @RequestParam(required = false) String timezone) {
        return offeringService.getTeacherOfferings(teacherId, timezone);
    }
}
