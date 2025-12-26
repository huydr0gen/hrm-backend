package com.tlu.hrm.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.entities.SpecialSchedule;
import com.tlu.hrm.enums.SpecialScheduleStatus;
import com.tlu.hrm.enums.SpecialScheduleType;

public interface SpecialScheduleRepository 
extends JpaRepository<SpecialSchedule, Long>, JpaSpecificationExecutor<SpecialSchedule> {

	// =====================================================
    // (1) Check trùng lịch CÙNG LOẠI (PENDING + APPROVED)
    // =====================================================
    @Query("""
        SELECT COUNT(s) > 0
        FROM SpecialSchedule s
        WHERE s.employee = :employee
          AND s.type = :type
          AND s.status IN :statuses
          AND s.startDate <= :endDate
          AND s.endDate >= :startDate
    """)
    boolean existsOverlappingSchedule(
            @Param("employee") Employee employee,
            @Param("type") SpecialScheduleType type,
            @Param("statuses") List<SpecialScheduleStatus> statuses,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // =====================================================
    // (2) Lấy Special Schedule ĐÃ DUYỆT theo NGÀY
    // (dùng cho import công)
    // =====================================================
    @Query("""
        SELECT s
        FROM SpecialSchedule s
        WHERE s.employee = :employee
          AND s.status = 'APPROVED'
          AND :date BETWEEN s.startDate AND s.endDate
    """)
    List<SpecialSchedule> findApprovedSchedulesByEmployeeAndDate(
            @Param("employee") Employee employee,
            @Param("date") LocalDate date
    );

    // =====================================================
    // (3) Lấy danh sách CHỜ DUYỆT theo approver
    // =====================================================
    List<SpecialSchedule> findByApproverIdAndStatus(
            Long approverId,
            SpecialScheduleStatus status
    );

    // =====================================================
    // (4) Optional: lấy lịch đang hiệu lực (nếu muốn 1 cái)
    // =====================================================
    @Query("""
        SELECT s
        FROM SpecialSchedule s
        WHERE s.employee = :employee
          AND s.status = 'APPROVED'
          AND :date BETWEEN s.startDate AND s.endDate
        ORDER BY s.createdAt DESC
    """)
    Optional<SpecialSchedule> findLatestApprovedByEmployeeAndDate(
            @Param("employee") Employee employee,
            @Param("date") LocalDate date
    );
}
