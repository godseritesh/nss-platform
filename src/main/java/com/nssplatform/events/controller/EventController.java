package com.nssplatform.events.controller;

import com.nssplatform.events.dto.EventRequest;
import com.nssplatform.events.dto.EventResponse;
import com.nssplatform.events.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<EventResponse> create(@Valid @RequestBody EventRequest req,
                                                 @AuthenticationPrincipal String email) {
        return ResponseEntity.status(201).body(eventService.createEvent(req, email));
    }

    @PutMapping("/api/admin/events/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody EventRequest req,
                                                 @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(eventService.updateEvent(id, req, email));
    }

    @DeleteMapping("/api/admin/events/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
