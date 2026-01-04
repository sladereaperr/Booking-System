package com.booking.backend.controller;

import com.booking.backend.model.Event;
import com.booking.backend.repository.BookingRepository;
import com.booking.backend.repository.EventRepository;
import com.booking.backend.repository.ShowRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin") // CHANGED: Removed "/events" from here
public class AdminController {

    @Autowired private EventRepository eventRepository;
    @Autowired private ShowRepository showRepository;
    @Autowired private BookingRepository bookingRepository;

    // ===========================
    // EVENT MANAGEMENT
    // ===========================

    @PostMapping("/events") // ADDED: "/events" here
    @PreAuthorize("hasRole('ADMIN')")
    public Event createEvent(@RequestBody Event event) {
        return eventRepository.save(event);
    }

    @DeleteMapping("/events/{id}") // ADDED: "/events" here
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEvent(@PathVariable Long id) {
        eventRepository.deleteById(id);
    }

    @PutMapping("/events/{id}") // ADDED: "/events" here
    @PreAuthorize("hasRole('ADMIN')")
    public com.booking.backend.model.Event updateEvent(@PathVariable Long id, @RequestBody com.booking.backend.model.Event updatedEvent) {
        return eventRepository.findById(id).map(event -> {
            if (updatedEvent.getTitle() != null) event.setTitle(updatedEvent.getTitle());
            if (updatedEvent.getGenre() != null) event.setGenre(updatedEvent.getGenre());
            if (updatedEvent.getDurationMinutes() != null) event.setDurationMinutes(updatedEvent.getDurationMinutes());
            if (updatedEvent.getLanguage() != null) event.setLanguage(updatedEvent.getLanguage());
            if (updatedEvent.getRating() != null) event.setRating(updatedEvent.getRating());
            return eventRepository.save(event);
        }).orElseThrow(() -> new RuntimeException("Event not found"));
    }

    // ===========================
    // SHOW MANAGEMENT
    // ===========================

    @PostMapping("/shows") // Remains "/shows" (Result: /api/admin/shows)
    @PreAuthorize("hasRole('ADMIN')")
    public com.booking.backend.model.Show createShow(@RequestBody com.booking.backend.model.Show show) {
        return showRepository.save(show);
    }

    @DeleteMapping("/shows/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteShow(@PathVariable Long id) {
        if (bookingRepository.existsByShowId(id)) {
            return ResponseEntity.badRequest().body("Cannot delete Show: Tickets have already been booked.");
        }
        showRepository.deleteById(id);
        return ResponseEntity.ok("Show deleted successfully");
    }

    @PutMapping("/shows/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateShow(@PathVariable Long id, @RequestBody com.booking.backend.model.Show updatedShow) {
        boolean hasBookings = bookingRepository.existsByShowId(id);
        
        return showRepository.findById(id).map(show -> {
            if (hasBookings) {
                if (updatedShow.getAuditorium() != null && !updatedShow.getAuditorium().getId().equals(show.getAuditorium().getId())) {
                    return ResponseEntity.badRequest().body("Cannot change Auditorium (Layout) after bookings have started.");
                }
            }
            if (updatedShow.getStartTime() != null) show.setStartTime(updatedShow.getStartTime());
            showRepository.save(show);
            return ResponseEntity.ok(show);
        }).orElse(ResponseEntity.notFound().build());
    }
}