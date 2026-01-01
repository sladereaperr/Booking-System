package com.booking.backend.controller;

import com.booking.backend.model.Booking;
import com.booking.backend.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // Endpoint: POST /api/bookings
    // Body: { "userId": 1, "showId": 1, "seatIds": [1, 2] }
    @PostMapping
    public ResponseEntity<?> bookTicket(@RequestBody Map<String, Object> payload) {
        try {
            // Extract data from JSON
            Long userId = ((Number) payload.get("userId")).longValue();
            Long showId = ((Number) payload.get("showId")).longValue();
            List<Integer> seatIdsInt = (List<Integer>) payload.get("seatIds");
            
            // Convert Integer list to Long list
            List<Long> seatIds = seatIdsInt.stream().map(Integer::longValue).toList();

            // Call the service (The logic we wrote earlier)
            Booking booking = bookingService.bookTicket(userId, showId, seatIds);
            
            return ResponseEntity.ok(booking);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}