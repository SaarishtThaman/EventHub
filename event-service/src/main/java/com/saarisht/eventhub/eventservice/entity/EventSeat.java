package com.saarisht.eventhub.eventservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "event_seats", uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "seat_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    Event event;

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    Seat seat;

    @Column(nullable = false)
    BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    SeatStatus status;
}
