package com.nssplatform.events.controller;

import com.nssplatform.events.dto.EventRequest;
import com.nssplatform.events.dto.EventResponse;
import com.nssplatform.events.service.EventService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/api/events")
    public ResponseEntity<Page<EventResponse>> list(
            @PageableDefault(size = 12, sort = "eventDate") Pageable pageable) {
        return ResponseEntity.ok(eventService.listEvents(pageable));
    }

    @GetMapping("/api/events/{id}")
    public ResponseEntity<EventResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEvent(id));
    }

    @PostMapping("/api/admin/events")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponse> create(
            @Valid @RequestBody EventRequest req,
            @NotEmpty(message = "Email is required") String email) {
        return ResponseEntity.status(201).body(eventService.createEvent(req, email));
    }

    @PutMapping("/api/admin/events/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody EventRequest req,
            @NotEmpty(message = "Email is required") String email,
            @DateTimeFormat(iso = ISO.DATE_TIME) ZonedDateTime start,
            @DateTimeFormat(iso = ISO.DATE_TIME) ZonedDateTime end) {
        return ResponseEntity.ok(eventService.updateEvent(id, req, email, start, end));
    }

    @DeleteMapping("/api/admin/events/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable @NotNull Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}