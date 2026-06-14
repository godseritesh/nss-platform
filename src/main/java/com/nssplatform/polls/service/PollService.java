package com.nssplatform.polls.service;

import com.nssplatform.auth.entity.User;
import com.nssplatform.auth.repository.UserRepository;
import com.nssplatform.events.entity.Event;
import com.nssplatform.events.repository.EventRepository;
import com.nssplatform.polls.dto.PollRequest;
import com.nssplatform.polls.dto.PollResponse;
import com.nssplatform.polls.entity.Poll;
import com.nssplatform.polls.entity.PollOption;
import com.nssplatform.polls.repository.PollOptionRepository;
import com.nssplatform.polls.repository.PollRepository;
import com.nssplatform.shared.exception.ResourceNotFoundException;
import com.nssplatform.shared.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<PollResponse> getPollsByEvent(Long eventId) {
        return pollRepository.findByEventIdWithOptions(eventId)
            .stream().map(PollResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public PollResponse getPoll(Long id) {
        Poll poll = findPollOrThrow(id);
        return PollResponse.from(poll);
    }

    @Transactional
    public PollResponse createPoll(Long eventId, PollRequest req, String adminEmail) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + eventId));
        User admin = findUserOrThrow(adminEmail);

        Poll poll = Poll.builder()
            .event(event)
            .question(req.getQuestion().trim())
            .expiresAt(req.getExpiresAt())
            .status(Poll.Status.ACTIVE)
            .createdBy(admin)
            .build();
        pollRepository.save(poll);

        AtomicInteger order = new AtomicInteger(0);
        req.getOptions().forEach(optText -> {
            PollOption opt = PollOption.builder()
                .poll(poll)
                .optionText(optText.trim())
                .displayOrder(order.getAndIncrement())
                .build();
            poll.getOptions().add(opt);
            pollOptionRepository.save(opt);
        });

        log.info("Poll created: id={} for eventId={}", poll.getId(), eventId);
        return PollResponse.from(poll);
    }

    @Transactional
    public PollResponse updatePoll(Long id, PollRequest req, String adminEmail) {
        Poll poll = findPollOrThrow(id);
        poll.setQuestion(req.getQuestion().trim());
        poll.setExpiresAt(req.getExpiresAt());

        // Soft-replace options: remove old, add new, keep existing votes
        pollOptionRepository.deleteByPollId(id);
        poll.getOptions().clear();
        AtomicInteger order = new AtomicInteger(0);
        req.getOptions().forEach(optText -> {
            PollOption opt = PollOption.builder()
                .poll(poll)
                .optionText(optText.trim())
                .displayOrder(order.getAndIncrement())
                .build();
            poll.getOptions().add(opt);
            pollOptionRepository.save(opt);
        });

        log.info("Poll updated: id={}", id);
        return PollResponse.from(poll);
    }

    @Transactional
    public void deletePoll(Long id) {
        Poll poll = findPollOrThrow(id);
        pollRepository.delete(poll);
        log.info("Poll deleted: id={}", id);
    }

    private Poll findPollOrThrow(Long id) {
        return pollRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Poll not found: " + id));
    }

    private User findUserOrThrow(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UnauthorizedException("Admin user not found"));
    }
}
