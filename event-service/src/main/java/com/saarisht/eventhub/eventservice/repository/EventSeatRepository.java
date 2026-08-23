package com.saarisht.eventhub.eventservice.repository;

import com.saarisht.eventhub.eventservice.entity.EventSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventSeatRepository extends JpaRepository<EventSeat, Long> {
    List<EventSeat> findByEventId(Long eventId);
}
