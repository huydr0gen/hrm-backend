package com.tlu.hrm.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tlu.hrm.entities.AttendanceRecord;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

	// Dùng cho import / update từng ngày
	Optional<AttendanceRecord> findByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);
	
    // Dùng cho export theo tháng
    List<AttendanceRecord> findByWorkDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("""
    	    SELECT ar
    	    FROM AttendanceRecord ar
    	    WHERE ar.employee.id = :employeeId
    	      AND ar.workDate BETWEEN :start AND :end
    	""")
    	List<AttendanceRecord> findMonthly(
    	        @Param("employeeId") Long employeeId,
    	        @Param("start") LocalDate start,
    	        @Param("end") LocalDate end
    	);
}
