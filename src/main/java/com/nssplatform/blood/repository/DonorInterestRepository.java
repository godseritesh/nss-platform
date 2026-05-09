package com.nssplatform.blood.repository;

import com.nssplatform.blood.entity.DonorInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonorInterestRepository extends JpaRepository<DonorInterest, Long> {
    List<DonorInterest> findByBloodRequestIdOrderByCreatedAtDesc(Long requestId);
    long countByBloodRequestId(Long requestId);
}
