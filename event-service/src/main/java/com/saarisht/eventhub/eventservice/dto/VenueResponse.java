package com.saarisht.eventhub.eventservice.dto;

import com.saarisht.eventhub.eventservice.entity.Venue;

public record VenueResponse(
        Long id,
        String name,
        String address,
        Integer seatingCapacity
) {
    public static VenueResponse from(Venue venue) {
        return new VenueResponse(venue.getId(), venue.getName(), venue.getAddress(), venue.getSeatingCapacity());
    }
}
