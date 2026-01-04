package com.booking.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendBookingConfirmation(String email, Long bookingId) {
        String message = "Booking Confirmed for ID: " + bookingId + " sent to " + email;
        kafkaTemplate.send("booking_notifications", message);
        System.out.println("📨 Message sent to Kafka topic");
    }
}