package com.nssplatform.votes.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data @Builder
public class PollResultResponse {
    private Long pollId;
    private String question;
    private long totalVotes;
    private List<OptionResult> results;

    @Data @Builder
    public static class OptionResult {
        private Long optionId;
        private String optionText;
        private long voteCount;
        private double percentage;
    }
}
