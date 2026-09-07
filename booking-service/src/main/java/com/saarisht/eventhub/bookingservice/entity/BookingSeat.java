package com.saarisht.eventhub.bookingservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "booking_seats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    /**
     * References event_seats.id in event-service's schema. No FK here —
     * that table is owned by a different service/schema, so this is an
     * opaque identifier the app trusts rather than a DB-enforced relation.
     */
    @Column(nullable = false)
    private Long eventSeatId;
}
