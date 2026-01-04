package com.booking.backend.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    @KafkaListener(topics = "booking_notifications", groupId = "notification-group")
    public void listen(String message) {
        // Requirement: Simulate sending email
        System.out.println("📧 EMAIL SENT: " + message);
    }
}