package com.booking.backend.service;

import com.booking.backend.model.*;
import com.booking.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShowRepository showRepository;

    // Inject NotificationProducer
    @Autowired private NotificationProducer notificationProducer;

    // This annotation ensures the method runs as a strict Transaction.
    // Isolation.SERIALIZABLE is the highest level of safety.
    // It prevents "Dirty Reads" and "Phantom Reads," ensuring no double-booking occurs.
    @Transactional 
    public Booking bookTicket(Long userId, Long showId, List<Long> seatIds) {
        
        // 1. Fetch User and Show (Basic Validation)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found"));

        // 2. Fetch the requested Seats
        List<Seat> seats = seatRepository.findAllByIdWithLock(seatIds);

        // Validation: Did we find all the seats requested?
        if (seats.size() != seatIds.size()) {
            throw new RuntimeException("Some seats were not found");
        }

        // 3. CRITICAL CHECK: Are any seats already BOOKED?
        // Because of @Transactional(SERIALIZABLE), the database locks these rows 
        // during this check, preventing other users from modifying them simultaneously.
        for (Seat seat : seats) {
            if (seat.getStatus() == SeatStatus.BOOKED) {
                throw new RuntimeException("Seat " + seat.getNumber() + " is already booked!");
            }
            // Optional: Check if seat actually belongs to the correct show
            if (!seat.getShow().getId().equals(showId)) {
                throw new RuntimeException("Seat does not belong to this show");
            }
        }

        // 4. Mark seats as BOOKED and Save
        for (Seat seat : seats) {
            seat.setStatus(SeatStatus.BOOKED);
        }
        seatRepository.saveAll(seats); // Update status in DB

        // 5. Create the Booking Record
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShow(show);
        booking.setSeats(seats);
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.CONFIRMED);

        // 6. Save and Return
        // At the END of bookTicket method:
        Booking savedBooking = bookingRepository.save(booking);
        // Send async notification (Requirement #6)
        try {
            notificationProducer.sendBookingConfirmation(user.getEmail(), savedBooking.getId());
        } catch (Exception e) {
            System.err.println("Notification failed, but booking is valid (Constraint met)");
        }
        return savedBooking;
    }
}