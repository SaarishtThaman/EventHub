package com.saarisht.eventhub.bookingservice.client;

import com.saarisht.eventhub.bookingservice.dto.EventSeatInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

/**
 * All calls out to event-service, which owns the event_seats table.
 * booking-service can't touch that table with a local DB update — it lives in
 * a different service's schema — so seat pricing/availability lookups and the
 * BOOKED-status confirmation both have to go through event-service's API.
 *
 * Confirmation is all-or-nothing on event-service's side: if any requested
 * seat is no longer AVAILABLE, the whole request fails and none of the seats
 * are marked BOOKED, avoiding partial-booking states.
 */
@Component
public class EventServiceClient {

    private final RestClient restClient;

    public EventServiceClient(RestClient.Builder restClientBuilder,
                               @Value("${eventhub.event-service.base-url}") String eventServiceBaseUrl) {
        this.restClient = restClientBuilder.baseUrl(eventServiceBaseUrl).build();
    }

    /**
     * The current seat map for an event, straight from event-service's own
     * DB — never trust seat prices or availability supplied by the client,
     * this is the only source of truth for both.
     */
    public List<EventSeatInfo> getEventSeats(Long eventId) {
        return restClient.get()
                .uri("/events/{eventId}/seats", eventId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<EventSeatInfo>>() {
                });
    }

    public boolean confirmSeats(Long eventId, List<Long> eventSeatIds) {
        try {
            restClient.post()
                    .uri("/events/{eventId}/seats/confirm", eventId)
                    .body(new ConfirmSeatsRequest(eventSeatIds))
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (RestClientResponseException e) {
            return false;
        }
    }

    private record ConfirmSeatsRequest(List<Long> eventSeatIds) {
    }
}
