package com.saarisht.eventhub.eventservice.repository;

import com.saarisht.eventhub.eventservice.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
}
