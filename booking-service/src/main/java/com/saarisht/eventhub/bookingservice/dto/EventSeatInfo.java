package com.saarisht.eventhub.bookingservice.dto;

import java.math.BigDecimal;

/**
 * Mirrors the fields of event-service's EventSeatResponse that booking-service
 * actually needs. Deliberately not the full response shape (no seat number,
 * section, tier) — booking-service only cares about price and availability.
 */
public record EventSeatInfo(Long eventSeatId, BigDecimal price, String status) {
}
