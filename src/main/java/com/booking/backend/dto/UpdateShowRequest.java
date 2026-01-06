package com.booking.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UpdateShowRequest {
    private Long eventId;

    private Long venueId;

    private Long auditoriumId;

    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;
}

