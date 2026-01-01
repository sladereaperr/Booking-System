package com.booking.backend.model;

import jakarta.persistence.*;
import lombok.Data; // Generates Getters, Setters, toString
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "users") // "user" is a reserved keyword in Postgres, so we use "users"
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // Will store the BCrypt hash

    @Enumerated(EnumType.STRING)
    private Role role; // ADMIN or USER
}