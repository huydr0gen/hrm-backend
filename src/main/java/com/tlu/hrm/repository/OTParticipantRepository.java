package com.tlu.hrm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tlu.hrm.entities.OTParticipant;

public interface OTParticipantRepository extends JpaRepository<OTParticipant, Long> {

	Page<OTParticipant> findByEmployeeId(Long employeeId, Pageable pageable);
}
