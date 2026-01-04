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

    @Autowired private UserRepository userRepository;
    @Autowired private EventRepository eventRepository;
    @Autowired private VenueRepository venueRepository;
    @Autowired private AuditoriumRepository auditoriumRepository;
    @Autowired private ShowRepository showRepository;
    @Autowired private SeatRepository seatRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🌱 Checking Data Seeding...");

        // 1. Ensure Admin Exists (Fixes "User not found" error)
        if (userRepository.findByEmail("admin@test.com").isEmpty()) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@test.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setPhone("1234567890");
            userRepository.save(admin);
            System.out.println("✅ ADMIN USER CREATED: admin@test.com / admin123");
        }

        // 2. Check if other data exists
        if (eventRepository.count() == 0) {
            System.out.println("🌱 Seeding Demo Events...");

            // Create Event
            Event event = new Event();
            event.setTitle("Inception");
            event.setGenre("Sci-Fi");
            event.setDurationMinutes(148);
            event.setLanguage("English");
            event.setRating(8.8);
            eventRepository.save(event);

            // Create Venue & Auditorium
            Venue venue = new Venue();
            venue.setName("PVR Cinemas");
            venue.setLocation("Downtown");
            venueRepository.save(venue);

            // Inside DataSeeder.java
            Auditorium audi = new Auditorium();
            audi.setName("IMAX Screen 1");
            audi.setVenue(venue);
            audi.setTotalRows(10);    // New
            audi.setTotalColumns(15); // New
            auditoriumRepository.save(audi);

            // Create Show
            Show show = new Show();
            show.setEvent(event);
            show.setAuditorium(audi);
            show.setVenue(venue);
            show.setStartTime(LocalDateTime.now().plusDays(1));
            showRepository.save(show);

            // Create Seats
            Seat s1 = new Seat(); s1.setShow(show); s1.setRow("A"); s1.setNumber(1); s1.setPrice(200.0); s1.setCategory("PREMIUM"); s1.setStatus(SeatStatus.AVAILABLE);
            Seat s2 = new Seat(); s2.setShow(show); s1.setRow("A"); s2.setNumber(2); s2.setPrice(200.0); s2.setCategory("PREMIUM"); s2.setStatus(SeatStatus.AVAILABLE);
            Seat s3 = new Seat(); s3.setShow(show); s3.setRow("B"); s3.setNumber(1); s3.setPrice(150.0); s3.setCategory("REGULAR"); s3.setStatus(SeatStatus.AVAILABLE);
            
            seatRepository.saveAll(Arrays.asList(s1, s2, s3));

            System.out.println("✅ DEMO DATA FULLY SEEDED");
        }
    }
}