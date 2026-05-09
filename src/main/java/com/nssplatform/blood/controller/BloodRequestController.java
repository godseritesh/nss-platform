package com.nssplatform.blood.controller;

import com.nssplatform.blood.dto.BloodRequestForm;
import com.nssplatform.blood.dto.BloodRequestResponse;
import com.nssplatform.blood.dto.DonorInterestForm;
import com.nssplatform.blood.entity.BloodRequest;
import com.nssplatform.blood.service.BloodRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blood-requests")
@RequiredArgsConstructor
public class BloodRequestController {

    private final BloodRequestService service;

    /** Public: list open requests */
    @GetMapping
    public ResponseEntity<Page<BloodRequestResponse>> list(
            @RequestParam(required = false) String bloodGroup,
            @PageableDefault(size = 12, sort = "deadline") Pageable pageable) {
        if (bloodGroup != null) {
            try {
                BloodRequest.BloodGroup bg = BloodRequest.BloodGroup.valueOf(bloodGroup);
                return ResponseEntity.ok(service.listByBloodGroup(bg, pageable));
            } catch (IllegalArgumentException ignored) {}
        }
        return ResponseEntity.ok(service.listOpen(pageable));
    }

    /** Public: all open requests for map */
    @GetMapping("/map")
    public ResponseEntity<List<BloodRequestResponse>> forMap() {
        return ResponseEntity.ok(service.listAllOpenForMap());
    }

    /** Public: single request detail */
    @GetMapping("/{id}")
    public ResponseEntity<BloodRequestResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.getOne(id));
    }

    /** Authenticated: submit a new request */
    @PostMapping
    public ResponseEntity<BloodRequestResponse> submit(
            @Valid @RequestBody BloodRequestForm form,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.status(201).body(service.submit(form, email));
    }

    /** Public: register donor interest */
    @PostMapping("/{id}/interest")
    public ResponseEntity<Void> interest(@PathVariable Long id,
                                          @Valid @RequestBody DonorInterestForm form) {
        service.registerInterest(id, form);
        return ResponseEntity.status(201).build();
    }

    /** Admin: mark fulfilled */
    @PatchMapping("/{id}/fulfill")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> fulfill(@PathVariable Long id) {
        service.fulfill(id);
        return ResponseEntity.noContent().build();
    }
}
