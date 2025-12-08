package com.tlu.hrm.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tlu.hrm.entities.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

	Page<AuditLog> findByUserId(Long userId, Pageable pageable);
	
	Page<AuditLog> findByActionContainingIgnoreCase(String action, Pageable pageable);
	
	Page<AuditLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
	
	Page<AuditLog> findByUserIdAndActionContainingIgnoreCase(Long userId, String action, Pageable pageable);
	
	Page<AuditLog> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);
	
	Page<AuditLog> findByActionContainingIgnoreCaseAndCreatedAtBetween(String action, LocalDateTime start, LocalDateTime end, Pageable pageable);
	
	Page<AuditLog> findByUserIdAndActionContainingIgnoreCaseAndCreatedAtBetween(
			Long userId,
			String action,
			LocalDateTime start,
			LocalDateTime end,
			Pageable pageable
	    );
}
