package com.saarisht.eventhub.eventservice.dto;

import java.util.List;

public record ConfirmSeatsRequest(List<Long> eventSeatIds) {
}
