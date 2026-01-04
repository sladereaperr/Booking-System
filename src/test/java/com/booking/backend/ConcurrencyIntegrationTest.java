package com.booking.backend; // CHECK YOUR PACKAGE NAME

import com.booking.backend.model.*;
import com.booking.backend.repository.*;
import com.booking.backend.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConcurrencyIntegrationTest extends AbstractIntegrationTest {

    @Autowired private BookingService bookingService;
    @Autowired private SeatRepository seatRepository;
    @Autowired private ShowRepository showRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private VenueRepository venueRepository;
    @Autowired private AuditoriumRepository auditoriumRepository;
    @Autowired private EventRepository eventRepository;

    @Test
    public void testDoubleBookingHandling() throws InterruptedException {
        // 1. Setup minimal data
        Venue venue = new Venue();
        venue.setName("Cinema");
        venue.setLocation("NY");
        venue = venueRepository.save(venue);

        Auditorium audi = new Auditorium();
        audi.setName("Hall 1");
        audi.setTotalRows(10);
        audi.setTotalColumns(10);
        audi.setVenue(venue);
        audi = auditoriumRepository.save(audi);

        Event event = new Event();
        event.setTitle("Movie");
        event.setGenre("Act");
        event.setDurationMinutes(100);
        event.setLanguage("En");
        event.setRating(5.0);
        event = eventRepository.save(event);

        Show show = new Show();
        show.setEvent(event);
        show.setVenue(venue);
        show.setAuditorium(audi);
        show.setStartTime(LocalDateTime.now().plusDays(1));
        show = showRepository.save(show);
        
        Seat seat = new Seat();
        seat.setShow(show);
        seat.setCategory("REG");
        seat.setPrice(10.0);
        seat.setNumber(1);
        seat.setRow("A"); // Ensure this matches your Seat field (rowNumber vs row)
        seat.setStatus(SeatStatus.AVAILABLE);
        seat = seatRepository.save(seat);

        User user1 = new User();
        user1.setEmail("u1@t.com");
        user1.setName("U1");
        user1.setPassword("pass");
        user1.setRole(Role.USER);
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("u2@t.com");
        user2.setName("U2");
        user2.setPassword("pass");
        user2.setRole(Role.USER);
        user2 = userRepository.save(user2);

        // 2. Setup Thread Pool
        int numberOfThreads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // Capture variables for lambda
        final Long showId = show.getId();
        final Long seatId = seat.getId();
        final Long u1Id = user1.getId();
        final Long u2Id = user2.getId();

        // 3. Define Tasks
        Runnable task1 = () -> {
            try {
                bookingService.bookTicket(u1Id, showId, Collections.singletonList(seatId));
                successCount.incrementAndGet();
            } catch (Exception e) {
                failureCount.incrementAndGet();
            } finally {
                latch.countDown();
            }
        };

        Runnable task2 = () -> {
            try {
                bookingService.bookTicket(u2Id, showId, Collections.singletonList(seatId));
                successCount.incrementAndGet();
            } catch (Exception e) {
                failureCount.incrementAndGet();
            } finally {
                latch.countDown();
            }
        };

        // 4. Run simultaneously
        executorService.submit(task1);
        executorService.submit(task2);

        latch.await(); // Wait for completion

        // 5. Assertions
        assertEquals(1, successCount.get(), "Exactly one booking should succeed");
        assertEquals(1, failureCount.get(), "Exactly one booking should fail");
        
        // Verify Seat Status is BOOKED
        Seat updatedSeat = seatRepository.findById(seatId).get();
        assertEquals(SeatStatus.BOOKED, updatedSeat.getStatus());
    }
}