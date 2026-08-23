package com.saarisht.eventhub.eventservice.controller;

import com.saarisht.eventhub.eventservice.dto.EventRequest;
import com.saarisht.eventhub.eventservice.dto.EventResponse;
import com.saarisht.eventhub.eventservice.dto.EventSeatResponse;
import com.saarisht.eventhub.eventservice.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<EventResponse> create(@Valid @RequestBody EventRequest request) {
        return ResponseEntity.ok(eventService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAll() {
        return ResponseEntity.ok(eventService.getAll());
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<List<EventSeatResponse>> getSeatsForEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getSeatsForEvent(id));
    }
}
