package com.saarisht.eventhub.bookingservice.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Holds what a payment attempt was "for," between the moment booking-service
 * initiates the charge and the moment payment-service's webhook confirms or
 * fails it. There is no Booking row yet at this point (by design — see
 * BookingStatus), so this is the only record of the attempt in the meantime.
 * Stored in Redis, keyed by paymentId, with a TTL — see PaymentContextService.
 */
public record PaymentContext(
        Long userId,
        Long eventId,
        List<Long> eventSeatIds,
        BigDecimal amount
) {
}
