package com.booking.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "venue", indexes = {
    @Index(name = "idx_venue_location", columnList = "location")
})
@Data
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;
}