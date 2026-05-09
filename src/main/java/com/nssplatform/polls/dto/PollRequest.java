package com.nssplatform.polls.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PollRequest {

    @NotBlank(message = "Question is required")
    @Size(max = 500, message = "Question must not exceed 500 characters")
    private String question;

    @Future(message = "Expiry must be in the future")
    private LocalDateTime expiresAt;

    @NotNull(message = "Options are required")
    @Size(min = 2, max = 10, message = "Poll must have between 2 and 10 options")
    private List<@NotBlank(message = "Option text cannot be blank") String> options;
}
