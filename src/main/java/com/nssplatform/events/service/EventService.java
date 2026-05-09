package com.nssplatform.events.service;

import com.nssplatform.auth.entity.User;
import com.nssplatform.auth.repository.UserRepository;
import com.nssplatform.events.dto.EventRequest;
import com.nssplatform.events.dto.EventResponse;
import com.nssplatform.events.entity.Event;
import com.nssplatform.events.repository.EventRepository;
import com.nssplatform.shared.exception.ResourceNotFoundException;
import com.nssplatform.shared.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<EventResponse> listEvents(Pageable pageable) {
        return eventRepository.findAllByOrderByEventDateDesc(pageable)
            .map(EventResponse::from);
    }

    @Transactional(readOnly = true)
    public EventResponse getEvent(Long id) {
        return EventResponse.from(findEventOrThrow(id));
    }

    @Transactional
    public EventResponse createEvent(EventRequest req, String adminEmail) {
        User admin = findUserOrThrow(adminEmail);
        Event event = Event.builder()
            .title(req.getTitle().trim())
            .description(req.getDescription())
            .eventDate(req.getEventDate())
            .location(req.getLocation())
            .category(req.getCategory())
            .createdBy(admin)
            .build();
        eventRepository.save(event);
        log.info("Event created: id={} by adminId={}", event.getId(), admin.getId());
        return EventResponse.from(event);
    }

    @Transactional
    public EventResponse updateEvent(Long id, EventRequest req, String adminEmail) {
        Event event = findEventOrThrow(id);
        event.setTitle(req.getTitle().trim());
        event.setDescription(req.getDescription());
        event.setEventDate(req.getEventDate());
        event.setLocation(req.getLocation());
        event.setCategory(req.getCategory());
        log.info("Event updated: id={}", id);
        return EventResponse.from(event);
    }

    @Transactional
    public void deleteEvent(Long id) {
        Event event = findEventOrThrow(id);
        eventRepository.delete(event);
        log.info("Event deleted: id={}", id);
    }

    private Event findEventOrThrow(Long id) {
        return eventRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id));
    }

    private User findUserOrThrow(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UnauthorizedException("Admin user not found"));
    }
}
