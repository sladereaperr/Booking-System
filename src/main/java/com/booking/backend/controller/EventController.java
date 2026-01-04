package com.booking.backend.controller;

import com.booking.backend.model.Event;
import com.booking.backend.service.EventService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired private EventService eventService ;

    // Updated Endpoint: Supports filtering
    // Usage: /api/events?city=New York&language=English
// Inside EventController.java
    @GetMapping
    public List<Event> getEvents(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) LocalDate date) {
        
        // Call the cleaned-up Service method
        return eventService.searchEvents(city, genre, language, date);
    }
}