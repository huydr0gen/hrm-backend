package com.tlu.hrm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tlu.hrm.entities.OTRequest;
import com.tlu.hrm.enums.OTRequestStatus;

public interface OTRequestRepository extends JpaRepository<OTRequest, Long> {

	Page<OTRequest> findByManagerId(Long managerId, Pageable pageable);

    Page<OTRequest> findByManagerIdAndStatus(
            Long managerId,
            OTRequestStatus status,
            Pageable pageable
    );
}
