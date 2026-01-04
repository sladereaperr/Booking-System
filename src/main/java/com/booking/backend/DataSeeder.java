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
    @Autowired private AuditoriumRepository auditoriumRepository; // New Repo
    @Autowired private ShowRepository showRepository;
    @Autowired private SeatRepository seatRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            System.out.println("🌱 SEEDING DATA...");

            // 1. Users (Admin & User)
            User admin = new User(null, "Admin", "admin@test.com", passwordEncoder.encode("admin123"), Role.ADMIN);
            User user = new User(null, "User", "user@test.com", passwordEncoder.encode("user123"), Role.USER);
            userRepository.saveAll(Arrays.asList(admin, user));

            // 2. Event
            Event event = new Event();
            event.setTitle("Inception");
            event.setGenre("Sci-Fi");
            event.setDurationMinutes(148);
            eventRepository.save(event);

            // 3. Venue & Auditorium
            Venue venue = new Venue();
            venue.setName("PVR Cinemas");
            venue.setLocation("Downtown");
            venueRepository.save(venue);

            Auditorium audi = new Auditorium();
            audi.setName("IMAX Screen 1");
            audi.setVenue(venue);
            auditoriumRepository.save(audi); // You might need to create this Repo interface if missing

            // 4. Show
            Show show = new Show();
            show.setEvent(event);
            show.setAuditorium(audi); // Linked to Audi, not just Venue
            show.setVenue(venue); // Keep this if you didn't remove it, otherwise remove
            show.setStartTime(LocalDateTime.now().plusDays(1));
            showRepository.save(show);

            // 5. Seats (Premium & Regular)
            Seat s1 = new Seat(); s1.setShow(show); s1.setRow("A"); s1.setNumber(1); s1.setPrice(200.0); s1.setCategory("PREMIUM"); s1.setStatus(SeatStatus.AVAILABLE);
            Seat s2 = new Seat(); s2.setShow(show); s1.setRow("A"); s2.setNumber(2); s2.setPrice(200.0); s2.setCategory("PREMIUM"); s2.setStatus(SeatStatus.AVAILABLE);
            Seat s3 = new Seat(); s3.setShow(show); s3.setRow("B"); s3.setNumber(1); s3.setPrice(150.0); s3.setCategory("REGULAR"); s3.setStatus(SeatStatus.AVAILABLE);
            
            seatRepository.saveAll(Arrays.asList(s1, s2, s3));

            System.out.println("✅ DEMO DATA READY!");
        }
    }
}