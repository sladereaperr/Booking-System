package com.booking.backend.controller;

import com.booking.backend.model.Show;
import com.booking.backend.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/shows")
public class ShowController {

    @Autowired private ShowRepository showRepository;

    // Requirement: View shows grouped by event (Get all shows for a Movie)
    // Usage: /api/shows/event/1
    @GetMapping("/event/{eventId}")
    public List<Show> getShowsByEvent(@PathVariable Long eventId) {
        return showRepository.findByEventId(eventId);
    }

    // Requirement: View show details including Seat availability (real-time)
    // Usage: /api/shows/1
    @GetMapping("/{id}")
    public Show getShowDetails(@PathVariable Long id) {
        // This returns the Show + The List of Seats (with status BOOKED/AVAILABLE)
        // Because mappedBy="show" is in the Entity, JPA fetches the fresh status from DB.
        return showRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Show not found"));
    }
}