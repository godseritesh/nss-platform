package com.nssplatform.votes;

import com.nssplatform.auth.entity.User;
import com.nssplatform.auth.repository.UserRepository;
import com.nssplatform.events.entity.Event;
import com.nssplatform.events.repository.EventRepository;
import com.nssplatform.polls.entity.Poll;
import com.nssplatform.polls.entity.PollOption;
import com.nssplatform.polls.repository.PollOptionRepository;
import com.nssplatform.polls.repository.PollRepository;
import com.nssplatform.shared.exception.ConflictException;
import com.nssplatform.shared.exception.ForbiddenException;
import com.nssplatform.votes.dto.VoteRequest;
import com.nssplatform.votes.repository.VoteRepository;
import com.nssplatform.votes.service.VoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock VoteRepository voteRepository;
    @Mock PollRepository pollRepository;
    @Mock PollOptionRepository pollOptionRepository;
    @Mock UserRepository userRepository;

    @InjectMocks VoteService voteService;

    private Poll activePoll;
    private Poll expiredPoll;
    private PollOption option;
    private User user;

    @BeforeEach
    void setup() {
        Event event = Event.builder().id(1L).title("Test Event")
            .category(Event.Category.BLOOD_DONATION).build();

        activePoll = Poll.builder().id(10L).event(event).question("?")
            .status(Poll.Status.ACTIVE)
            .expiresAt(LocalDateTime.now().plusDays(1))
            .options(new ArrayList<>()).build();

        expiredPoll = Poll.builder().id(11L).event(event).question("Old?")
            .status(Poll.Status.ACTIVE)
            .expiresAt(LocalDateTime.now().minusDays(1))
            .options(new ArrayList<>()).build();

        option = PollOption.builder().id(100L).poll(activePoll)
            .optionText("Yes").displayOrder(0).build();
        activePoll.getOptions().add(option);

        user = User.builder().id(1L).name("Test User")
            .email("user@test.com").role(User.Role.ROLE_USER).build();
    }

    @Test
    void successfulVote() {
        when(pollRepository.findById(10L)).thenReturn(Optional.of(activePoll));
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(voteRepository.existsByUserIdAndPollId(1L, 10L)).thenReturn(false);
        when(pollOptionRepository.findById(100L)).thenReturn(Optional.of(option));

        VoteRequest req = new VoteRequest();
        req.setPollOptionId(100L);
        voteService.castVote(10L, req, "user@test.com");

        verify(voteRepository).save(any());
    }

    @Test
    void duplicateVoteThrowsConflict() {
        when(pollRepository.findById(10L)).thenReturn(Optional.of(activePoll));
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(voteRepository.existsByUserIdAndPollId(1L, 10L)).thenReturn(true);

        VoteRequest req = new VoteRequest();
        req.setPollOptionId(100L);

        assertThatThrownBy(() -> voteService.castVote(10L, req, "user@test.com"))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("already voted");
    }

    @Test
    void expiredPollThrowsForbidden() {
        when(pollRepository.findById(11L)).thenReturn(Optional.of(expiredPoll));

        VoteRequest req = new VoteRequest();
        req.setPollOptionId(100L);

        assertThatThrownBy(() -> voteService.castVote(11L, req, "user@test.com"))
            .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void wrongOptionPollThrowsException() {
        Poll otherPoll = Poll.builder().id(99L)
            .event(activePoll.getEvent()).question("?")
            .status(Poll.Status.ACTIVE)
            .expiresAt(LocalDateTime.now().plusDays(1))
            .options(new ArrayList<>()).build();

        PollOption foreignOption = PollOption.builder().id(200L).poll(otherPoll)
            .optionText("No").displayOrder(0).build();

        when(pollRepository.findById(10L)).thenReturn(Optional.of(activePoll));
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(voteRepository.existsByUserIdAndPollId(1L, 10L)).thenReturn(false);
        when(pollOptionRepository.findById(200L)).thenReturn(Optional.of(foreignOption));

        VoteRequest req = new VoteRequest();
        req.setPollOptionId(200L);

        assertThatThrownBy(() -> voteService.castVote(10L, req, "user@test.com"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("does not belong");
    }
}
