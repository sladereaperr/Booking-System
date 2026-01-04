package com.booking.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

@Entity
@Table(name = "event", indexes = {
    @Index(name = "idx_event_title", columnList = "title"),
    @Index(name = "idx_event_genre", columnList = "genre")
})
@Data
public class Event implements Serializable {
    
    private static final long serialVersionUID = 1L; 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String genre;
    private Integer durationMinutes;

    // --- NEW FIELDS ---
    private String language; // e.g., "English", "Hindi"
    private Double rating;   // e.g., 4.5, 8.2
}