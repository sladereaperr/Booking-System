package com.booking.backend.controller;

import com.booking.backend.model.Event;
import com.booking.backend.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/events")
// Ensure SecurityConfig enables @EnableMethodSecurity
public class AdminController {

    @Autowired private EventRepository eventRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Requirement: Only Admin
    public Event createEvent(@RequestBody Event event) {
        return eventRepository.save(event);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEvent(@PathVariable Long id) {
        eventRepository.deleteById(id);
    }
}