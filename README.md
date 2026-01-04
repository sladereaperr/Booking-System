# Online Booking System - Backend Intern Project

## Overview

A Spring Boot backend for an event booking system featuring ACID compliant transactions, Redis caching, JWT security, and Docker containerization.

## Tech Stack

- **Language:** Java 21 (Spring Boot 3)
- **Database:** PostgreSQL 15
- **Cache:** Redis
- **Security:** Spring Security + JWT (BCrypt hashing)
- **Containerization:** Docker & Docker Compose

## Features

1.  **User Authentication:** Secure Login/Registration with JWT.
2.  **Event Browsing:** Cached using Redis for high performance.
3.  **Ticket Booking:** Prevents double-booking using Serialized Transaction Isolation.
4.  **Dockerized:** Full stack runs with a single command.

## How to Run

1.  Ensure Docker Desktop is running.
2.  Run the stack:
    ```bash
    docker-compose up --build
    ```
3.  API will be available at `http://localhost:8080`.

## API Endpoints

- `POST /api/auth/login` - Get JWT Token
- `GET /api/events` - List Events (Cached)
- `POST /api/bookings` - Book a ticket (Requires Auth)
