package com.saarisht.eventhub.eventservice.controller;

import com.saarisht.eventhub.eventservice.dto.VenueRequest;
import com.saarisht.eventhub.eventservice.dto.VenueResponse;
import com.saarisht.eventhub.eventservice.service.VenueService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/venues")
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @PostMapping
    public ResponseEntity<VenueResponse> create(@Valid @RequestBody VenueRequest request) {
        return ResponseEntity.ok(venueService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VenueResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(venueService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<VenueResponse>> getAll() {
        return ResponseEntity.ok(venueService.getAll());
    }
}
