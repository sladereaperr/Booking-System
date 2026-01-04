package com.booking.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "show_id", nullable = false)
    @JsonIgnore
    private Show show;

    @Column(name = "row_number")
    private String row; // e.g., "A", "B"

    @Column(name = "seat_number")
    private int number; // e.g., 1, 2

    @Enumerated(EnumType.STRING)
    private SeatStatus status; // AVAILABLE, BOOKED

    private double price;
    
    // Optimistic locking version (Advanced protection against double booking)
    @Version
    private Integer version;

    // Inside Seat.java
    @Column(nullable = false)
    private String category; // e.g., "PREMIUM", "REGULAR"
}