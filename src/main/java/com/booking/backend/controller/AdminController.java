package com.booking.backend.controller;

import com.booking.backend.dto.CreateEventRequest;
import com.booking.backend.dto.UpdateEventRequest;
import com.booking.backend.dto.CreateShowRequest;
import com.booking.backend.dto.UpdateShowRequest;
import com.booking.backend.model.Event;
import com.booking.backend.model.Show;
import com.booking.backend.model.Venue;
import com.booking.backend.model.Auditorium;
import com.booking.backend.repository.BookingRepository;
import com.booking.backend.repository.EventRepository;
import com.booking.backend.repository.ShowRepository;
import com.booking.backend.repository.VenueRepository;
import com.booking.backend.repository.AuditoriumRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private EventRepository eventRepository;
    @Autowired private ShowRepository showRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private VenueRepository venueRepository;
    @Autowired private AuditoriumRepository auditoriumRepository;

    // ===========================
    // EVENT MANAGEMENT
    // ===========================

    @PostMapping("/events")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Event> createEvent(@RequestBody @Valid CreateEventRequest request) {
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setGenre(request.getGenre());
        event.setDurationMinutes(request.getDurationMinutes());
        event.setLanguage(request.getLanguage());
        event.setRating(request.getRating());
        
        Event savedEvent = eventRepository.save(event);
        return ResponseEntity.ok(savedEvent);
    }

    @DeleteMapping("/events/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        eventRepository.deleteById(id);
        return ResponseEntity.ok("Event deleted successfully");
    }

    @PutMapping("/events/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody @Valid UpdateEventRequest request) {
        return eventRepository.findById(id).map(event -> {
            if (request.getTitle() != null) event.setTitle(request.getTitle());
            if (request.getGenre() != null) event.setGenre(request.getGenre());
            if (request.getDurationMinutes() != null) event.setDurationMinutes(request.getDurationMinutes());
            if (request.getLanguage() != null) event.setLanguage(request.getLanguage());
            if (request.getRating() != null) event.setRating(request.getRating());
            Event updatedEvent = eventRepository.save(event);
            return ResponseEntity.ok(updatedEvent);
        }).orElse(ResponseEntity.notFound().build());
    }

    // ===========================
    // SHOW MANAGEMENT
    // ===========================

    @PostMapping("/shows")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Show> createShow(@RequestBody @Valid CreateShowRequest request) {
        // Fetch related entities
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + request.getEventId()));
        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new RuntimeException("Venue not found with ID: " + request.getVenueId()));
        Auditorium auditorium = auditoriumRepository.findById(request.getAuditoriumId())
                .orElseThrow(() -> new RuntimeException("Auditorium not found with ID: " + request.getAuditoriumId()));

        Show show = new Show();
        show.setEvent(event);
        show.setVenue(venue);
        show.setAuditorium(auditorium);
        show.setStartTime(request.getStartTime());

        Show savedShow = showRepository.save(show);
        return ResponseEntity.ok(savedShow);
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
    public ResponseEntity<?> updateShow(@PathVariable Long id, @RequestBody @Valid UpdateShowRequest request) {
        boolean hasBookings = bookingRepository.existsByShowId(id);
        
        return showRepository.findById(id).map(show -> {
            if (hasBookings) {
                if (request.getAuditoriumId() != null && !request.getAuditoriumId().equals(show.getAuditorium().getId())) {
                    return ResponseEntity.badRequest().body("Cannot change Auditorium (Layout) after bookings have started.");
                }
            }
            
            // Update fields if provided
            if (request.getEventId() != null) {
                Event event = eventRepository.findById(request.getEventId())
                        .orElseThrow(() -> new RuntimeException("Event not found with ID: " + request.getEventId()));
                show.setEvent(event);
            }
            if (request.getVenueId() != null) {
                Venue venue = venueRepository.findById(request.getVenueId())
                        .orElseThrow(() -> new RuntimeException("Venue not found with ID: " + request.getVenueId()));
                show.setVenue(venue);
            }
            if (request.getAuditoriumId() != null) {
                Auditorium auditorium = auditoriumRepository.findById(request.getAuditoriumId())
                        .orElseThrow(() -> new RuntimeException("Auditorium not found with ID: " + request.getAuditoriumId()));
                show.setAuditorium(auditorium);
            }
            if (request.getStartTime() != null) {
                show.setStartTime(request.getStartTime());
            }
            
            Show updatedShow = showRepository.save(show);
            return ResponseEntity.ok(updatedShow);
        }).orElse(ResponseEntity.notFound().build());
    }
}