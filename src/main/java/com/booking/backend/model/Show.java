package com.booking.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "shows")
@Data
public class Show {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    private LocalDateTime startTime;
    
    // Helper to see seats for this show (Optional but useful)
    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL)
    private List<Seat> seats;

    // Inside Show.java
    @ManyToOne
    @JoinColumn(name = "auditorium_id")
    private Auditorium auditorium;
}