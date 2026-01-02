package com.tlu.hrm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tlu.hrm.entities.AttendanceImportHistory;

public interface AttendanceImportHistoryRepository extends JpaRepository<AttendanceImportHistory, Long> {

	Page<AttendanceImportHistory> findByMonth(
            String month,
            Pageable pageable
    );
}
