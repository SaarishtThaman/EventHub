package com.saarisht.eventhub.eventservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    Venue venue;

    @Column(nullable = false)
    String section;

    @Column(nullable = false)
    String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    SeatTier tier;
}
