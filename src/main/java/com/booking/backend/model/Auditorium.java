package com.booking.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Auditorium {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name; // e.g., "Screen 1"
    
    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;
}