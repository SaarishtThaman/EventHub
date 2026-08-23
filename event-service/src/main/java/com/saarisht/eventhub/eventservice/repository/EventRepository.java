package com.saarisht.eventhub.eventservice.repository;

import com.saarisht.eventhub.eventservice.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
