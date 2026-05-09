package com.nssplatform.events.dto;

import com.nssplatform.events.entity.Event;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EventRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 5000, message = "Description too long")
    private String description;

    private LocalDate eventDate;

    @Size(max = 200, message = "Location too long")
    private String location;

    @NotNull(message = "Category is required")
    private Event.Category category;
}
