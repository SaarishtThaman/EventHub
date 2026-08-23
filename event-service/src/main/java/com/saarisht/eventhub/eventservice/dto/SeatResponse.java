package com.saarisht.eventhub.eventservice.dto;

import com.saarisht.eventhub.eventservice.entity.Seat;
import com.saarisht.eventhub.eventservice.entity.SeatTier;

public record SeatResponse(
        Long id,
        Long venueId,
        String section,
        String seatNumber,
        SeatTier tier
) {
    public static SeatResponse from(Seat seat) {
        return new SeatResponse(
                seat.getId(),
                seat.getVenue().getId(),
                seat.getSection(),
                seat.getSeatNumber(),
                seat.getTier()
        );
    }
}
