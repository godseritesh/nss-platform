package com.nssplatform.analytics.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class BloodDonationStats {
    private long totalBloodDonationEvents;
    private long totalBloodDonationVotes;
    private long usersEngaged;
    private double increasePercentage;
    private String impactSummary;
}
