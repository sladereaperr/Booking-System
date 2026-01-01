package com.booking.backend.repository;

import com.booking.backend.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ShowRepository extends JpaRepository<Show, Long> {
    List<Show> findByEventId(Long eventId); // Find all shows for a specific movie
}