package com.booking.backend.controller;

import com.booking.backend.model.Event;
import com.booking.backend.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*; // Importing all annotations
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping
    public List<Event> getEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/search")
    public List<Event> searchEvents(@RequestParam String query) {
        // FIXED: Call the service, not the repository directly
        return eventService.searchEvents(query);
    }
}