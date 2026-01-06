package com.booking.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateEventRequest {
    @Size(max = 200, message = "Title must be less than 200 characters")
    private String title;

    private String genre;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 500, message = "Duration must be less than 500 minutes")
    private Integer durationMinutes;

    private String language;

    @DecimalMin(value = "0.0", message = "Rating must be at least 0.0")
    @DecimalMax(value = "10.0", message = "Rating must be at most 10.0")
    private Double rating;
}

