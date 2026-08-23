package com.saarisht.eventhub.eventservice.controller;

import com.saarisht.eventhub.eventservice.dto.SeatRequest;
import com.saarisht.eventhub.eventservice.dto.SeatResponse;
import com.saarisht.eventhub.eventservice.service.SeatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/venues/{venueId}/seats")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @PostMapping
    public ResponseEntity<List<SeatResponse>> createSeats(
            @PathVariable Long venueId,
            @Valid @RequestBody List<SeatRequest> requests
    ) {
        return ResponseEntity.ok(seatService.createSeats(venueId, requests));
    }

    @GetMapping
    public ResponseEntity<List<SeatResponse>> getSeatsForVenue(@PathVariable Long venueId) {
        return ResponseEntity.ok(seatService.getSeatsForVenue(venueId));
    }
}
