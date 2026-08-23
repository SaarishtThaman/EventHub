package com.saarisht.eventhub.eventservice.dto;

import com.saarisht.eventhub.eventservice.entity.Event;

import java.time.Instant;

public record EventResponse(
        Long id,
        Long venueId,
        String name,
        String performer,
        String category,
        Instant startTime
) {
    public static EventResponse from(Event event) {
        return new EventResponse(
                event.getId(),
                event.getVenue().getId(),
                event.getName(),
                event.getPerformer(),
                event.getCategory(),
                event.getStartTime()
        );
    }
}
