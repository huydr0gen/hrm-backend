package com.tlu.hrm.repository;

import java.time.LocalDate;
import java.time.LocalTime;
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
    
 // Tổng số ngày làm việc trong tháng
    @Query("""
        SELECT COUNT(DISTINCT ar.workDate)
        FROM AttendanceRecord ar
        WHERE FUNCTION('to_char', ar.workDate, 'YYYY-MM') = :month
    """)
    Integer countWorkingDays(@Param("month") String month);

    // Tổng số lượt đi muộn
    @Query("""
        SELECT COUNT(ar)
        FROM AttendanceRecord ar
        WHERE ar.checkIn IS NOT NULL
          AND ar.checkIn > :lateTime
          AND FUNCTION('to_char', ar.workDate, 'YYYY-MM') = :month
    """)
    Integer countLate(
            @Param("month") String month,
            @Param("lateTime") LocalTime lateTime
    );

    // Số nhân viên đi muộn
    @Query("""
        SELECT COUNT(DISTINCT ar.employee.id)
        FROM AttendanceRecord ar
        WHERE ar.checkIn IS NOT NULL
          AND ar.checkIn > :lateTime
          AND FUNCTION('to_char', ar.workDate, 'YYYY-MM') = :month
    """)
    Integer countLateEmployees(
            @Param("month") String month,
            @Param("lateTime") LocalTime lateTime
    );

    // Tổng OT phút
    @Query("""
        SELECT COALESCE(SUM(ar.otMinutes), 0)
        FROM AttendanceRecord ar
        WHERE FUNCTION('to_char', ar.workDate, 'YYYY-MM') = :month
    """)
    Integer sumOtMinutes(@Param("month") String month);

    // Số nhân viên có OT
    @Query("""
        SELECT COUNT(DISTINCT ar.employee.id)
        FROM AttendanceRecord ar
        WHERE ar.otMinutes > 0
          AND FUNCTION('to_char', ar.workDate, 'YYYY-MM') = :month
    """)
    Integer countOtEmployees(@Param("month") String month);
    
}
