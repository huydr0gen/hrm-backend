package com.tlu.hrm.repository.dashboard;

import java.time.LocalTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tlu.hrm.entities.AttendanceRecord;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {

	@Query("""
        SELECT COUNT(ar)
        FROM AttendanceRecord ar
        WHERE ar.checkIn IS NOT NULL
          AND ar.checkIn > :lateTime
          AND FUNCTION('to_char', ar.workDate, 'YYYY-MM') = :month
    """)
    Integer countLate(String month, LocalTime lateTime);

    @Query("""
        SELECT COUNT(DISTINCT ar.employee.id)
        FROM AttendanceRecord ar
        WHERE ar.checkIn IS NOT NULL
          AND ar.checkIn > :lateTime
          AND FUNCTION('to_char', ar.workDate, 'YYYY-MM') = :month
    """)
    Integer countLateEmployees(String month, LocalTime lateTime);

    @Query("""
        SELECT COUNT(DISTINCT ar.workDate)
        FROM AttendanceRecord ar
        WHERE FUNCTION('to_char', ar.workDate, 'YYYY-MM') = :month
    """)
    Integer countWorkingDays(String month);

    @Query("""
        SELECT COALESCE(SUM(ar.otMinutes), 0)
        FROM AttendanceRecord ar
        WHERE FUNCTION('to_char', ar.workDate, 'YYYY-MM') = :month
    """)
    Integer sumOtMinutes(String month);

    @Query("""
        SELECT COUNT(DISTINCT ar.employee.id)
        FROM AttendanceRecord ar
        WHERE ar.otMinutes > 0
          AND FUNCTION('to_char', ar.workDate, 'YYYY-MM') = :month
    """)
    Integer countOtEmployees(String month);
}
