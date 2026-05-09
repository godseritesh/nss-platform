package com.nssplatform.votes.controller;

import com.nssplatform.votes.dto.PollResultResponse;
import com.nssplatform.votes.dto.VoteRequest;
import com.nssplatform.votes.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/api/polls/{pollId}/vote")
    public ResponseEntity<Void> vote(@PathVariable Long pollId,
                                      @Valid @RequestBody VoteRequest req,
                                      @AuthenticationPrincipal String email) {
        voteService.castVote(pollId, req, email);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/api/polls/{pollId}/results")
    public ResponseEntity<PollResultResponse> results(@PathVariable Long pollId) {
        return ResponseEntity.ok(voteService.getResults(pollId));
    }
}
