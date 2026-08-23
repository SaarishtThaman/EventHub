package com.saarisht.eventhub.eventservice.dto;

import com.saarisht.eventhub.eventservice.entity.SeatTier;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SeatRequest(
        @NotBlank String section,
        @NotBlank String seatNumber,
        @NotNull SeatTier tier
) {
}
