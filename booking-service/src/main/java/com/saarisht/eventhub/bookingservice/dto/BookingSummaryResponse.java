package com.saarisht.eventhub.bookingservice.dto;

import com.saarisht.eventhub.bookingservice.entity.Booking;
import com.saarisht.eventhub.bookingservice.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record BookingSummaryResponse(
        Long id,
        Long eventId,
        BigDecimal totalAmount,
        BookingStatus status,
        Instant createdAt,
        List<Long> eventSeatIds
) {
    public static BookingSummaryResponse from(Booking booking) {
        return new BookingSummaryResponse(
                booking.getId(),
                booking.getEventId(),
                booking.getTotalAmount(),
                booking.getStatus(),
                booking.getCreatedAt(),
                booking.getSeats().stream().map(s -> s.getEventSeatId()).toList()
        );
    }
}
