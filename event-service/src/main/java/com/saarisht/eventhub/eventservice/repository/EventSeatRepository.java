package com.saarisht.eventhub.eventservice.repository;

import com.saarisht.eventhub.eventservice.entity.EventSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventSeatRepository extends JpaRepository<EventSeat, Long> {
    // Ordered explicitly by seat id — without it, Postgres doesn't guarantee
    // row order between calls, and an UPDATE (e.g. confirming a seat as
    // BOOKED) can relocate a row physically, making the seat map visibly
    // reshuffle on the next fetch.
    List<EventSeat> findByEventIdOrderBySeatId(Long eventId);

    /**
     * The correctness backstop for booking confirmation: only flips seats that
     * are still AVAILABLE, and only if they belong to the given event. Returns
     * how many rows were actually updated, so the caller can tell a full
     * success apart from a partial one (some seat was already booked/gone) —
     * see SeatService.confirmSeats.
     */
    @Modifying
    @Query("UPDATE EventSeat es SET es.status = com.saarisht.eventhub.eventservice.entity.SeatStatus.BOOKED " +
            "WHERE es.id IN :eventSeatIds AND es.event.id = :eventId " +
            "AND es.status = com.saarisht.eventhub.eventservice.entity.SeatStatus.AVAILABLE")
    int confirmSeats(@Param("eventId") Long eventId, @Param("eventSeatIds") List<Long> eventSeatIds);
}
