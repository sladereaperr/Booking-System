package com.booking.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Auditorium {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name; // "Screen 1"
    
    // --- NEW FIELDS ---
    private int totalRows;    // e.g., 20
    private int totalColumns; // e.g., 15
    
    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;
}