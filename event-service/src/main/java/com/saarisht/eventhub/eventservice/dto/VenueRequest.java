package com.saarisht.eventhub.eventservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record VenueRequest(
        @NotBlank String name,
        @NotBlank String address,
        @Positive Integer seatingCapacity
) {
}
