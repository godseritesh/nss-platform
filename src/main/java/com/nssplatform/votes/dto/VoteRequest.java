package com.nssplatform.votes.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VoteRequest {
    @NotNull(message = "Poll option ID is required")
    private Long pollOptionId;
}
