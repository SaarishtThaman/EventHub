package com.saarisht.eventhub.eventservice.dto;

import com.saarisht.eventhub.eventservice.entity.EventSeat;
import com.saarisht.eventhub.eventservice.entity.SeatStatus;
import com.saarisht.eventhub.eventservice.entity.SeatTier;

import java.math.BigDecimal;

public record EventSeatResponse(
        Long eventSeatId,
        Long seatId,
        String section,
        String seatNumber,
        SeatTier tier,
        BigDecimal price,
        SeatStatus status
) {
    public static EventSeatResponse from(EventSeat eventSeat) {
        return new EventSeatResponse(
                eventSeat.getId(),
                eventSeat.getSeat().getId(),
                eventSeat.getSeat().getSection(),
                eventSeat.getSeat().getSeatNumber(),
                eventSeat.getSeat().getTier(),
                eventSeat.getPrice(),
                eventSeat.getStatus()
        );
    }
}
