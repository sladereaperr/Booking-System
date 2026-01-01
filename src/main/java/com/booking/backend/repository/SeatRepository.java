package com.booking.backend.repository;

import com.booking.backend.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    // Standard CRUD is enough for now
}