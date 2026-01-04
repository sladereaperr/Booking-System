package com.booking.backend.service;

import com.booking.backend.model.Event;
import com.booking.backend.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j // 1. Uses Lombok for proper Logging
@RequiredArgsConstructor // 2. Uses Constructor Injection (Better than @Autowired)
public class EventService {

    private final EventRepository eventRepository;
    private static final String CACHE_NAME = "events";

    /**
     * Fetches all events.
     * Uses Redis Caching to avoid hitting the DB repeatedly.
     */
    @Cacheable(CACHE_NAME) 
    public List<Event> getAllEvents() {
        log.info("⚠️ Cache Miss: Fetching all events from Database..."); 
        return eventRepository.findAll();
    }

    /**
     * Advanced Search for Events.
     * Filters by City, Genre, Language, and Date.
     * Note: We typically DO NOT cache search results due to the high number of variations.
     */
    public List<Event> searchEvents(String city, String genre, String language, LocalDate date) {
        log.debug("Searching events with filters - City: {}, Genre: {}, Lang: {}, Date: {}", city, genre, language, date);
        
        // Handle the case where no filters are provided (return all)
        if (city == null && genre == null && language == null && date == null) {
            return this.getAllEvents(); // Use the internal cached method
        }

        return eventRepository.searchEvents(
            city, 
            genre, 
            language, 
            (date != null) ? date.atStartOfDay() : null
        );
    }

    /**
     * Fetch a single event by ID.
     */
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + id));
    }
}