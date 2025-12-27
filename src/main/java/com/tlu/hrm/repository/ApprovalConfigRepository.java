package com.tlu.hrm.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tlu.hrm.entities.ApprovalConfig;
import com.tlu.hrm.enums.ApprovalTargetType;

public interface ApprovalConfigRepository extends JpaRepository<ApprovalConfig, Long> {

	Optional<ApprovalConfig>
    findByTargetTypeAndTargetIdAndActiveTrue(
        ApprovalTargetType targetType,
        Long targetId
    );
	
	Page<ApprovalConfig> findByTargetTypeAndActiveTrue(
	    ApprovalTargetType targetType,
	    Pageable pageable
	);
}
