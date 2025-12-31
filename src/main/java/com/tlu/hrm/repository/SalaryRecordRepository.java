package com.tlu.hrm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tlu.hrm.entities.SalaryRecord;

public interface SalaryRecordRepository extends JpaRepository<SalaryRecord, Long> {

	Optional<SalaryRecord> findByEmployeeIdAndMonthAndYear(
        Long employeeId, Integer month, Integer year);

	boolean existsByEmployeeIdAndMonthAndYear(
        Long employeeId, Integer month, Integer year);
	
	// Tổng lương cơ bản
    @Query("""
        SELECT COALESCE(SUM(sr.basicSalary), 0)
        FROM SalaryRecord sr
        WHERE sr.month = :month
          AND sr.year = :year
    """)
    Long sumBasicSalary(
            @Param("month") Integer month,
            @Param("year") Integer year
    );

    // Tổng tiền OT
    @Query("""
        SELECT COALESCE(SUM(sr.otPay), 0)
        FROM SalaryRecord sr
        WHERE sr.month = :month
          AND sr.year = :year
    """)
    Long sumOtPay(
            @Param("month") Integer month,
            @Param("year") Integer year
    );

    // Tổng thu nhập (đã trừ / cộng)
    @Query("""
        SELECT COALESCE(SUM(sr.totalSalary), 0)
        FROM SalaryRecord sr
        WHERE sr.month = :month
          AND sr.year = :year
    """)
    Long sumTotalSalary(
            @Param("month") Integer month,
            @Param("year") Integer year
    );

    // Thu nhập trung bình
    @Query("""
        SELECT COALESCE(AVG(sr.totalSalary), 0)
        FROM SalaryRecord sr
        WHERE sr.month = :month
          AND sr.year = :year
    """)
    Long avgTotalSalary(
            @Param("month") Integer month,
            @Param("year") Integer year
    );
    
    @Modifying
    @Query("""
        delete from SalaryRecord s
        where s.month = :month and s.year = :year
    """)
    void deleteByMonthAndYear(int month, int year);
}
