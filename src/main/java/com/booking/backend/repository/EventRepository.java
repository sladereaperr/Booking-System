package com.booking.backend.repository;

import com.booking.backend.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    // Requirement: Browse events by city, date, genre, or language.
    @Query("SELECT DISTINCT e FROM Event e " +
           "JOIN Show s ON s.event = e " +
           "JOIN Venue v ON s.venue = v " +
           "WHERE (:city IS NULL OR v.location ILIKE %:city%) " +
           "AND (:genre IS NULL OR e.genre ILIKE %:genre%) " +
           "AND (:language IS NULL OR e.language ILIKE %:language%) " +
           "AND (cast(:startDate as timestamp) IS NULL OR s.startTime >= :startDate)")
    List<Event> searchEvents(
            @Param("city") String city,
            @Param("genre") String genre,
            @Param("language") String language,
            @Param("startDate") LocalDateTime startDate
    );
}