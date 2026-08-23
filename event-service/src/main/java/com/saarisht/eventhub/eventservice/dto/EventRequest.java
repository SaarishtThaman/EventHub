package com.saarisht.eventhub.eventservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

public record EventRequest(
        @NotNull Long venueId,
        @NotBlank String name,
        @NotBlank String performer,
        @NotBlank String category,
        @NotNull Instant startTime,
        @NotNull @Positive BigDecimal price
) {
}
