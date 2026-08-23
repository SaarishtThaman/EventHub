package com.saarisht.eventhub.eventservice.service;

import com.saarisht.eventhub.eventservice.dto.VenueRequest;
import com.saarisht.eventhub.eventservice.dto.VenueResponse;
import com.saarisht.eventhub.eventservice.entity.Venue;
import com.saarisht.eventhub.eventservice.repository.VenueRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class VenueService {

    private final VenueRepository venueRepository;

    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    public VenueResponse create(VenueRequest request) {
        Venue venue = new Venue();
        venue.setName(request.name());
        venue.setAddress(request.address());
        venue.setSeatingCapacity(request.seatingCapacity());

        return VenueResponse.from(venueRepository.save(venue));
    }

    public VenueResponse getById(Long id) {
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Venue not found: " + id));
        return VenueResponse.from(venue);
    }

    public List<VenueResponse> getAll() {
        return venueRepository.findAll().stream()
                .map(VenueResponse::from)
                .toList();
    }
}
