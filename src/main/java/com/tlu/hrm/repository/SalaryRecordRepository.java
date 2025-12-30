package com.tlu.hrm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tlu.hrm.entities.SalaryRecord;

public interface SalaryRecordRepository extends JpaRepository<SalaryRecord, Long> {

	Optional<SalaryRecord> findByEmployeeIdAndMonthAndYear(
        Long employeeId, Integer month, Integer year);

	boolean existsByEmployeeIdAndMonthAndYear(
        Long employeeId, Integer month, Integer year);
	
	// Tổng lương cơ bản trong tháng
    @Query("""
        SELECT COALESCE(SUM(sr.baseSalary), 0)
        FROM SalaryRecord sr
        WHERE sr.month = :month
          AND sr.year = :year
    """)
    Long sumBaseSalary(
            @Param("month") Integer month,
            @Param("year") Integer year
    );

    // Tổng lương OT trong tháng
    @Query("""
        SELECT COALESCE(SUM(sr.otSalary), 0)
        FROM SalaryRecord sr
        WHERE sr.month = :month
          AND sr.year = :year
    """)
    Long sumOtSalary(
            @Param("month") Integer month,
            @Param("year") Integer year
    );

    // Lương trung bình
    @Query("""
        SELECT COALESCE(AVG(sr.baseSalary), 0)
        FROM SalaryRecord sr
        WHERE sr.month = :month
          AND sr.year = :year
    """)
    Long avgSalary(
            @Param("month") Integer month,
            @Param("year") Integer year
    );
}
