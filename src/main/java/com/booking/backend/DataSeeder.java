package com.booking.backend;

import com.booking.backend.model.*;
import com.booking.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private UserRepository userRepository;
    @Autowired private EventRepository eventRepository;
    @Autowired private VenueRepository venueRepository;
    @Autowired private ShowRepository showRepository;
    @Autowired private SeatRepository seatRepository;

    @Override
    public void run(String... args) throws Exception {
        // Only seed if DB is empty
        if (userRepository.count() == 0) {
            
            // 1. Create User
            User user = new User();
            user.setName("John Doe");
            user.setEmail("john@example.com");
            user.setPassword(passwordEncoder.encode("password123")); // Real BCrypt hash
            user.setRole(Role.USER);
            userRepository.save(user);

            // 2. Create Event (Movie)
            Event event = new Event();
            event.setTitle("Avengers: Endgame");
            event.setGenre("Action");
            event.setDurationMinutes(180);
            eventRepository.save(event);

            // 3. Create Venue
            Venue venue = new Venue();
            venue.setName("IMAX Cinema");
            venue.setLocation("New York");
            venueRepository.save(venue);

            // 4. Create Show
            Show show = new Show();
            show.setEvent(event);
            show.setVenue(venue);
            show.setStartTime(LocalDateTime.now().plusDays(1));
            showRepository.save(show);

            // 5. Create Seats for the Show
            Seat seat1 = new Seat();
            seat1.setShow(show);
            seat1.setRow("A");
            seat1.setNumber(1);
            seat1.setPrice(100.0);
            seat1.setStatus(SeatStatus.AVAILABLE);
            
            Seat seat2 = new Seat();
            seat2.setShow(show);
            seat2.setRow("A");
            seat2.setNumber(2);
            seat2.setPrice(100.0);
            seat2.setStatus(SeatStatus.AVAILABLE);

            seatRepository.saveAll(Arrays.asList(seat1, seat2));

            System.out.println("✅ DUMMY DATA INSERTED");
        }
    }
}