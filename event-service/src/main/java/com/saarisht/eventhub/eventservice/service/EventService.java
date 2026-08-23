package com.saarisht.eventhub.eventservice.service;

import com.saarisht.eventhub.eventservice.dto.EventRequest;
import com.saarisht.eventhub.eventservice.dto.EventResponse;
import com.saarisht.eventhub.eventservice.dto.EventSeatResponse;
import com.saarisht.eventhub.eventservice.entity.Event;
import com.saarisht.eventhub.eventservice.entity.EventSeat;
import com.saarisht.eventhub.eventservice.entity.Seat;
import com.saarisht.eventhub.eventservice.entity.SeatStatus;
import com.saarisht.eventhub.eventservice.entity.Venue;
import com.saarisht.eventhub.eventservice.repository.EventRepository;
import com.saarisht.eventhub.eventservice.repository.EventSeatRepository;
import com.saarisht.eventhub.eventservice.repository.SeatRepository;
import com.saarisht.eventhub.eventservice.repository.VenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final SeatRepository seatRepository;
    private final EventSeatRepository eventSeatRepository;

    public EventService(
            EventRepository eventRepository,
            VenueRepository venueRepository,
            SeatRepository seatRepository,
            EventSeatRepository eventSeatRepository
    ) {
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
        this.seatRepository = seatRepository;
        this.eventSeatRepository = eventSeatRepository;
    }

    @Transactional
    public EventResponse create(EventRequest request) {
        Venue venue = venueRepository.findById(request.venueId())
                .orElseThrow(() -> new NoSuchElementException("Venue not found: " + request.venueId()));

        Event event = new Event();
        event.setVenue(venue);
        event.setName(request.name());
        event.setPerformer(request.performer());
        event.setCategory(request.category());
        event.setStartTime(request.startTime());
        event = eventRepository.save(event);

        List<Seat> venueSeats = seatRepository.findByVenueId(venue.getId());
        if (venueSeats.isEmpty()) {
            throw new IllegalStateException("Venue has no seats configured: " + venue.getId());
        }

        Event savedEvent = event;
        List<EventSeat> eventSeats = venueSeats.stream()
                .map(seat -> {
                    EventSeat eventSeat = new EventSeat();
                    eventSeat.setEvent(savedEvent);
                    eventSeat.setSeat(seat);
                    eventSeat.setPrice(request.price());
                    eventSeat.setStatus(SeatStatus.AVAILABLE);
                    return eventSeat;
                })
                .toList();
        eventSeatRepository.saveAll(eventSeats);

        return EventResponse.from(savedEvent);
    }

    public EventResponse getById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found: " + id));
        return EventResponse.from(event);
    }

    public List<EventResponse> getAll() {
        return eventRepository.findAll().stream()
                .map(EventResponse::from)
                .toList();
    }

    public List<EventSeatResponse> getSeatsForEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NoSuchElementException("Event not found: " + eventId);
        }

        return eventSeatRepository.findByEventId(eventId).stream()
                .map(EventSeatResponse::from)
                .toList();
    }
}
