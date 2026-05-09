package com.nssplatform.analytics.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class OverviewStats {
    private long totalUsers;
    private long totalEvents;
    private long totalPolls;
    private long totalVotes;
    private long activePolls;
    private long bloodDonationEvents;
    private long bloodDonationVotes;
    private long adminCount;
    private double estimatedBloodDonationIncrease;
}
