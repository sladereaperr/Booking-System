package com.booking.backend.repository;

import com.booking.backend.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    // Basic search implementation
    List<Event> findByTitleContainingIgnoreCaseOrGenreContainingIgnoreCase(String title, String genre);
}