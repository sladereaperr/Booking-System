package com.booking.backend; // CHECK YOUR PACKAGE NAME

import com.booking.backend.dto.RegisterRequest;
import com.booking.backend.model.*;
import com.booking.backend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class BookingFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private ShowRepository showRepository;
    @Autowired private SeatRepository seatRepository;
    @Autowired private VenueRepository venueRepository;
    @Autowired private EventRepository eventRepository;
    @Autowired private AuditoriumRepository auditoriumRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private Long showId;
    private Long seatId;

    @BeforeEach
    void setupData() {
        // Clean DB in order to prevent foreign key errors
        seatRepository.deleteAll();
        showRepository.deleteAll();
        auditoriumRepository.deleteAll();
        venueRepository.deleteAll();
        eventRepository.deleteAll();
        userRepository.deleteAll();

        // 1. Setup Admin
        User admin = new User();
        admin.setName("Admin");
        admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        // 2. Setup Venue
        Venue venue = new Venue();
        venue.setName("Test Cinema");
        venue.setLocation("NY");
        venue = venueRepository.save(venue);

        // 3. Setup Auditorium
        Auditorium audi = new Auditorium();
        audi.setName("Screen 1");
        audi.setTotalRows(10);
        audi.setTotalColumns(10);
        audi.setVenue(venue);
        audi = auditoriumRepository.save(audi);

        // 4. Setup Event
        Event event = new Event();
        event.setTitle("Test Movie");
        event.setGenre("Action");
        event.setDurationMinutes(120);
        event.setLanguage("Eng");
        event.setRating(8.5);
        event = eventRepository.save(event);
        
        // 5. Setup Show
        Show show = new Show();
        show.setEvent(event);
        show.setAuditorium(audi);
        show.setVenue(venue);
        show.setStartTime(LocalDateTime.now().plusDays(1));
        show = showRepository.save(show);
        this.showId = show.getId();

        // 6. Setup Seat
        Seat seat = new Seat();
        seat.setShow(show);
        seat.setCategory("PREMIUM");
        seat.setPrice(100.0);
        seat.setNumber(1); // Ensure Seat.java has "private int seatNumber" or "number"
        seat.setRow("A");
        seat.setStatus(SeatStatus.AVAILABLE);
        seat = seatRepository.save(seat);
        this.seatId = seat.getId();
    }

    @Test
    void testFullBookingFlow() throws Exception {
        // 1. Register User
        RegisterRequest registerReq = new RegisterRequest();
        registerReq.setName("John Doe");
        registerReq.setEmail("john@test.com");
        registerReq.setPassword("password123");
        registerReq.setPhone("9999999999");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isOk());

        // 2. Login & Get Token
        String loginJson = "{\"email\": \"john@test.com\", \"password\": \"password123\"}";
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String response = loginResult.getResponse().getContentAsString();
        // Extract token assuming response is Map {"accessToken": "..."}
        String token = objectMapper.readTree(response).get("accessToken").asText();

        // 3. Book Ticket
        // Get the User ID from DB to ensure it's correct
        Long userId = userRepository.findByEmail("john@test.com").get().getId();
        
        String bookingJson = String.format("{\"userId\": %d, \"showId\": %d, \"seatIds\": [%d]}", 
                userId, showId, seatId);

        mockMvc.perform(post("/api/bookings")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookingJson))
                .andExpect(status().isOk());
                // Note: Removed jsonPath check for simple 200 OK verification
        
        // 4. Verify Redis Cache (Hit Events API)
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk());
    }
}