package com.booking.backend.service;

import com.booking.backend.model.Event;
import com.booking.backend.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    // "events" is the name of the cache bucket in Redis
    @Cacheable("events") 
    public List<Event> getAllEvents() {
        System.out.println("⚠️ Fetching events from Database... (Not Cached)"); 
        // If you see this print log, it means cache MISS.
        // If you DON'T see this, it means cache HIT.
        return eventRepository.findAll();
    }
}