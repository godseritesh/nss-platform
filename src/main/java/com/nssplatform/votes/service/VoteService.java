package com.nssplatform.votes.service;

import com.nssplatform.auth.entity.User;
import com.nssplatform.auth.repository.UserRepository;
import com.nssplatform.polls.entity.Poll;
import com.nssplatform.polls.entity.PollOption;
import com.nssplatform.polls.repository.PollOptionRepository;
import com.nssplatform.polls.repository.PollRepository;
import com.nssplatform.shared.exception.ConflictException;
import com.nssplatform.shared.exception.ForbiddenException;
import com.nssplatform.shared.exception.ResourceNotFoundException;
import com.nssplatform.shared.exception.UnauthorizedException;
import com.nssplatform.votes.dto.PollResultResponse;
import com.nssplatform.votes.dto.VoteRequest;
import com.nssplatform.votes.entity.Vote;
import com.nssplatform.votes.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final UserRepository userRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void castVote(Long pollId, VoteRequest req, String userEmail) {
        Poll poll = pollRepository.findById(pollId)
            .orElseThrow(() -> new ResourceNotFoundException("Poll not found: " + pollId));

        if (!poll.isAcceptingVotes()) {
            throw new ForbiddenException("This poll is no longer accepting votes");
        }

        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (voteRepository.existsByUserIdAndPollId(user.getId(), pollId)) {
            throw new ConflictException("You have already voted on this poll");
        }

        PollOption option = pollOptionRepository.findById(req.getPollOptionId())
            .orElseThrow(() -> new ResourceNotFoundException("Poll option not found"));

        if (!option.getPoll().getId().equals(pollId)) {
            throw new IllegalArgumentException("Option does not belong to this poll");
        }

        Vote vote = Vote.builder()
            .user(user)
            .poll(poll)
            .pollOption(option)
            .build();
        try {
            voteRepository.save(vote);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("You have already voted on this poll");
        }
        log.info("Vote cast: pollId={} userId={}", pollId, user.getId());
    }

    @Transactional(readOnly = true)
    public PollResultResponse getResults(Long pollId) {
        Poll poll = pollRepository.findById(pollId)
            .orElseThrow(() -> new ResourceNotFoundException("Poll not found: " + pollId));

        List<Object[]> rawCounts = voteRepository.countVotesByOption(pollId);
        Map<Long, Long> countByOption = rawCounts.stream()
            .collect(Collectors.toMap(r -> (Long) r[0], r -> (Long) r[1]));

        long total = countByOption.values().stream().mapToLong(Long::longValue).sum();

        List<PollResultResponse.OptionResult> results = poll.getOptions().stream()
            .map(opt -> {
                long votes = countByOption.getOrDefault(opt.getId(), 0L);
                double pct = total > 0 ? (votes * 100.0 / total) : 0.0;
                return PollResultResponse.OptionResult.builder()
                    .optionId(opt.getId())
                    .optionText(opt.getOptionText())
                    .voteCount(votes)
                    .percentage(Math.round(pct * 10.0) / 10.0)
                    .build();
            })
            .toList();

        return PollResultResponse.builder()
            .pollId(pollId)
            .question(poll.getQuestion())
            .totalVotes(total)
            .results(results)
            .build();
    }
}
