package com.saarisht.eventhub.bookingservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookingRequest(
        @NotNull Long eventId,
        @NotEmpty List<Long> eventSeatIds
) {
}
