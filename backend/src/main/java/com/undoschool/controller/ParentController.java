package com.undoschool.controller;

import com.undoschool.dto.request.BookOfferingRequest;
import com.undoschool.dto.request.CreateParentRequest;
import com.undoschool.dto.response.BookingResponse;
import com.undoschool.dto.response.OfferingResponse;
import com.undoschool.entity.Booking;
import com.undoschool.entity.Parent;
import com.undoschool.service.OfferingService;
import com.undoschool.service.ParentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
public class ParentController {

    private final ParentService parentService;
    private final OfferingService offeringService;

    public ParentController(ParentService parentService, OfferingService offeringService) {
        this.parentService = parentService;
        this.offeringService = offeringService;
    }

    @PostMapping("/parents")
    @ResponseStatus(HttpStatus.CREATED)
    public Parent createParent(@Valid @RequestBody CreateParentRequest req) {
        return parentService.create(req);
    }

    @GetMapping("/offerings")
    public List<OfferingResponse> getOfferings(@RequestParam(required = false) String timezone) {
        return offeringService.getAllOfferings(timezone);
    }

    @PostMapping("/parents/{parentId}/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public Booking bookOffering(@PathVariable UUID parentId, @Valid @RequestBody BookOfferingRequest req) {
        return parentService.book(parentId, req);
    }

    @GetMapping("/parents/{parentId}/bookings")
    public List<BookingResponse> getBookings(@PathVariable UUID parentId,
                                              @RequestParam(required = false) String timezone) {
        return parentService.getBookings(parentId, timezone);
    }
}
