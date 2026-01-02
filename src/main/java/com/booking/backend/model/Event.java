package com.booking.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable; // <--- Import this

@Entity
@Data
public class Event implements Serializable { // <--- Add this interface
    
    // Add a version ID to prevent warnings (optional but good practice)
    private static final long serialVersionUID = 1L; 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String genre;
    private Integer durationMinutes;
}