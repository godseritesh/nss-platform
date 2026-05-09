package com.nssplatform.blood.service;

import com.nssplatform.auth.entity.User;
import com.nssplatform.auth.repository.UserRepository;
import com.nssplatform.blood.dto.BloodRequestForm;
import com.nssplatform.blood.dto.BloodRequestResponse;
import com.nssplatform.blood.dto.DonorInterestForm;
import com.nssplatform.blood.entity.BloodRequest;
import com.nssplatform.blood.entity.DonorInterest;
import com.nssplatform.blood.repository.BloodRequestRepository;
import com.nssplatform.blood.repository.DonorInterestRepository;
import com.nssplatform.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BloodRequestService {

    private final BloodRequestRepository requestRepo;
    private final DonorInterestRepository interestRepo;
    private final UserRepository userRepo;

    @Transactional(readOnly = true)
    public Page<BloodRequestResponse> listOpen(Pageable pageable) {
        return requestRepo.findByStatusOrderByUrgencyAscDeadlineAsc(BloodRequest.Status.OPEN, pageable)
            .map(r -> BloodRequestResponse.from(r, interestRepo.countByBloodRequestId(r.getId())));
    }

    @Transactional(readOnly = true)
    public Page<BloodRequestResponse> listByBloodGroup(BloodRequest.BloodGroup bg, Pageable pageable) {
        return requestRepo.findByStatusAndBloodGroupOrderByDeadlineAsc(BloodRequest.Status.OPEN, bg, pageable)
            .map(r -> BloodRequestResponse.from(r, interestRepo.countByBloodRequestId(r.getId())));
    }

    @Transactional(readOnly = true)
    public List<BloodRequestResponse> listAllOpenForMap() {
        return requestRepo.findAllOpenOrderedByUrgency().stream()
            .map(r -> BloodRequestResponse.from(r, interestRepo.countByBloodRequestId(r.getId())))
            .toList();
    }

    @Transactional(readOnly = true)
    public BloodRequestResponse getOne(Long id) {
        BloodRequest r = find(id);
        return BloodRequestResponse.from(r, interestRepo.countByBloodRequestId(id));
    }

    @Transactional
    public BloodRequestResponse submit(BloodRequestForm form, String userEmail) {
        Optional<User> user = userRepo.findByEmail(userEmail);
        BloodRequest req = BloodRequest.builder()
            .patientName(form.getPatientName().trim())
            .bloodGroup(form.getBloodGroup())
            .unitsNeeded(form.getUnitsNeeded())
            .hospital(form.getHospital().trim())
            .city(form.getCity().trim())
            .district(form.getDistrict().trim())
            .latitude(form.getLatitude())
            .longitude(form.getLongitude())
            .contactName(form.getContactName().trim())
            .contactPhone(form.getContactPhone().trim())
            .contactEmail(form.getContactEmail())
            .description(form.getDescription())
            .urgency(form.getUrgency())
            .deadline(form.getDeadline())
            .status(BloodRequest.Status.OPEN)
            .createdBy(user.orElse(null))
            .build();
        requestRepo.save(req);
        log.info("Blood request submitted: id={} bloodGroup={} city={}", req.getId(), req.getBloodGroup(), req.getCity());
        return BloodRequestResponse.from(req, 0);
    }

    @Transactional
    public void registerInterest(Long requestId, DonorInterestForm form) {
        BloodRequest req = find(requestId);
        DonorInterest interest = DonorInterest.builder()
            .bloodRequest(req)
            .name(form.getName().trim())
            .phone(form.getPhone().trim())
            .email(form.getEmail())
            .message(form.getMessage())
            .build();
        interestRepo.save(interest);
        log.info("Donor interest registered: requestId={}", requestId);
    }

    @Transactional
    public void fulfill(Long id) {
        BloodRequest req = find(id);
        req.setStatus(BloodRequest.Status.FULFILLED);
        log.info("Blood request fulfilled: id={}", id);
    }

    private BloodRequest find(Long id) {
        return requestRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Blood request not found: " + id));
    }
}
