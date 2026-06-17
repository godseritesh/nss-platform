package com.nssplatform.polls.controller;

import com.nssplatform.polls.dto.PollRequest;
import com.nssplatform.polls.dto.PollResponse;
import com.nssplatform.polls.service.PollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PollController {

    private final PollService pollService;

    @GetMapping("/api/events/{eventId}/polls")
    public ResponseEntity<List<PollResponse>> byEvent(@PathVariable Long eventId) {
        if (eventId == null || eventId <= 0) {
            throw new IllegalArgumentException("Event ID must be a positive number");
        }
        return ResponseEntity.ok(pollService.getPollsByEvent(eventId));
    }

    @GetMapping("/api/polls/{id}")
    public ResponseEntity<PollResponse> getById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Poll ID must be a positive number");
        }
        return ResponseEntity.ok(pollService.getPoll(id));
    }

    @PostMapping("/api/admin/events/{eventId}/polls")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PollResponse> create(@PathVariable Long eventId,
                                                 @Valid @RequestBody PollRequest req,
                                                 @AuthenticationPrincipal String email) {
        if (eventId == null || eventId <= 0) {
            throw newIllegalArgumentException("Event ID must be a positive number");
        }
        if (req == null || req.getQuestion() == null || req.getOptions() == null) {
            throw newIllegalArgumentException("Invalid poll request");
        }
        return ResponseEntity.status(201).body(pollService.createPoll(eventId, req, email));
    }

    @PutMapping("/api/admin/polls/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PollResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody PollRequest req,
                                                 @AuthenticationPrincipal String email) {
        if (id == null || id <= 0) {
            throw newIllegalArgumentException("Poll ID must be a positive number");
        }
        if (req == null || req.getQuestion() == null || req.getOptions() == null) {
            throw newIllegalArgumentException("Invalid poll request");
        }
        return ResponseEntity.ok(pollService.updatePoll(id, req, email));
    }

    @DeleteMapping("/api/admin/polls/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw newIllegalArgumentException("Poll ID must be a positive number");
        }
        pollService.deletePoll(id);
        return ResponseEntity.noContent().build();
    }

    private IllegalArgumentException newIllegalArgumentException(String message) {
        return new IllegalArgumentException(message);
    }
}