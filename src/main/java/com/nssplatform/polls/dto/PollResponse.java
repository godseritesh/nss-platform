package com.nssplatform.polls.dto;

import com.nssplatform.polls.entity.Poll;
import com.nssplatform.polls.entity.PollOption;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data @Builder
public class PollResponse {
    private Long id;
    private Long eventId;
    private String question;
    private LocalDateTime expiresAt;
    private String status;
    private boolean expired;
    private boolean acceptingVotes;
    private LocalDateTime createdAt;
    private List<OptionDto> options;

    @Data @Builder
    public static class OptionDto {
        private Long id;
        private String optionText;
        private Integer displayOrder;
    }

    public static PollResponse from(Poll p) {
        List<OptionDto> opts = p.getOptions().stream()
            .map(o -> OptionDto.builder()
                .id(o.getId())
                .optionText(o.getOptionText())
                .displayOrder(o.getDisplayOrder())
                .build())
            .toList();
        return PollResponse.builder()
            .id(p.getId())
            .eventId(p.getEvent().getId())
            .question(p.getQuestion())
            .expiresAt(p.getExpiresAt())
            .status(p.getStatus().name())
            .expired(p.isExpired())
            .acceptingVotes(p.isAcceptingVotes())
            .createdAt(p.getCreatedAt())
            .options(opts)
            .build();
    }
}
