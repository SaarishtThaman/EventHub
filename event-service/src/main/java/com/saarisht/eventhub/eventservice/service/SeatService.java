package com.saarisht.eventhub.eventservice.service;

import com.saarisht.eventhub.eventservice.dto.SeatRequest;
import com.saarisht.eventhub.eventservice.dto.SeatResponse;
import com.saarisht.eventhub.eventservice.entity.Seat;
import com.saarisht.eventhub.eventservice.entity.Venue;
import com.saarisht.eventhub.eventservice.repository.SeatRepository;
import com.saarisht.eventhub.eventservice.repository.VenueRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SeatService {

    private final SeatRepository seatRepository;
    private final VenueRepository venueRepository;

    public SeatService(SeatRepository seatRepository, VenueRepository venueRepository) {
        this.seatRepository = seatRepository;
        this.venueRepository = venueRepository;
    }

    public List<SeatResponse> createSeats(Long venueId, List<SeatRequest> requests) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new NoSuchElementException("Venue not found: " + venueId));

        List<Seat> seats = requests.stream()
                .map(request -> {
                    Seat seat = new Seat();
                    seat.setVenue(venue);
                    seat.setSection(request.section());
                    seat.setSeatNumber(request.seatNumber());
                    seat.setTier(request.tier());
                    return seat;
                })
                .toList();

        return seatRepository.saveAll(seats).stream()
                .map(SeatResponse::from)
                .toList();
    }

    public List<SeatResponse> getSeatsForVenue(Long venueId) {
        return seatRepository.findByVenueId(venueId).stream()
                .map(SeatResponse::from)
                .toList();
    }
}
