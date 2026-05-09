package com.nssplatform.analytics.controller;

import com.nssplatform.analytics.dto.BloodDonationStats;
import com.nssplatform.analytics.dto.OverviewStats;
import com.nssplatform.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/admin/analytics/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OverviewStats> overview() {
        return ResponseEntity.ok(analyticsService.getOverview());
    }

    @GetMapping("/admin/analytics/blood-donation-impact")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BloodDonationStats> bloodDonationImpact() {
        return ResponseEntity.ok(analyticsService.getBloodDonationImpact());
    }

    @GetMapping("/analytics/public/stats")
    public ResponseEntity<OverviewStats> publicStats() {
        return ResponseEntity.ok(analyticsService.getOverview());
    }
}
