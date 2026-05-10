package com.nssplatform.blood.repository;

import com.nssplatform.blood.entity.BloodRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {

    Page<BloodRequest> findByStatusOrderByUrgencyAscCreatedAtDesc(BloodRequest.Status status, Pageable pageable);

    Page<BloodRequest> findByStatusAndBloodGroupOrderByCreatedAtDesc(
            BloodRequest.Status status, BloodRequest.BloodGroup bloodGroup, Pageable pageable);

    @Query("SELECT r FROM BloodRequest r WHERE r.status = 'OPEN' ORDER BY r.urgency ASC, r.createdAt DESC")
    List<BloodRequest> findAllOpenOrderedByUrgency();

    long countByStatus(BloodRequest.Status status);

    long countByUrgencyAndStatus(BloodRequest.Urgency urgency, BloodRequest.Status status);

    @Query("SELECT COUNT(r) FROM BloodRequest r WHERE r.status = 'FULFILLED'")
    long countFulfilled();

    @Query("SELECT SUM(r.unitsNeeded) FROM BloodRequest r WHERE r.status = 'FULFILLED'")
    Long sumUnitsFulfilled();
}
