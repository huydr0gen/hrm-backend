package com.tlu.hrm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tlu.hrm.entities.SalaryRecord;

public interface SalaryRecordRepository extends JpaRepository<SalaryRecord, Long> {

	Optional<SalaryRecord> findByEmployeeIdAndMonthAndYear(
        Long employeeId, Integer month, Integer year);

	boolean existsByEmployeeIdAndMonthAndYear(
        Long employeeId, Integer month, Integer year);
}
