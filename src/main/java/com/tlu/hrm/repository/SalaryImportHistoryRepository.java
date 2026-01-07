package com.tlu.hrm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tlu.hrm.entities.SalaryImportHistory;

public interface SalaryImportHistoryRepository extends JpaRepository<SalaryImportHistory, Long> {

	Page<SalaryImportHistory> findByMonth(String month, Pageable pageable);
	
}
