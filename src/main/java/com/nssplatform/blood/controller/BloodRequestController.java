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
import org.springframework.data.domain.Sort;
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
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            if (bloodGroup != null) {
                BloodRequest.BloodGroup bg = BloodRequest.BloodGroup.valueOf(bloodGroup);
                if (bg == null) {
                    return ResponseEntity.badRequest().build();
                }
                return ResponseEntity.ok(service.listByBloodGroup(bg, pageable));
            } else {
                return ResponseEntity.ok(service.listOpen(pageable));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /** Public: all open requests for map */
    @GetMapping("/map")
    public ResponseEntity<List<BloodRequestResponse>> forMap() {
        try {
            return ResponseEntity.ok(service.listAllOpenForMap());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /** Public: single request detail */
    @GetMapping("/{id}")
    public ResponseEntity<BloodRequestResponse> getOne(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(service.getOne(id));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /** Authenticated: submit a new request */
    @PostMapping
    public ResponseEntity<BloodRequestResponse> submit(
            @Valid @RequestBody BloodRequestForm form,
            @AuthenticationPrincipal String email) {
        try {
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.status(201).body(service.submit(form, email));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /** Public: register donor interest */
    @PostMapping("/{id}/interest")
    public ResponseEntity<Void> interest(@PathVariable Long id,
                                          @Valid @RequestBody DonorInterestForm form) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().build();
            }
            service.registerInterest(id, form);
            return ResponseEntity.status(201).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /** Admin: mark fulfilled */
    @PatchMapping("/{id}/fulfill")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> fulfill(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().build();
            }
            service.fulfill(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}