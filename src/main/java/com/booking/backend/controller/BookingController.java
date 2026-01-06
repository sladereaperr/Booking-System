package com.booking.backend.controller;

import com.booking.backend.dto.BookingRequest;
import com.booking.backend.model.Booking;
import com.booking.backend.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> bookTicket(@RequestBody @Valid BookingRequest request) {
        try {
            Booking booking = bookingService.bookTicket(
                request.getUserId(), 
                request.getShowId(), 
                request.getSeatIds()
            );
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}