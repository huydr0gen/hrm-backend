package com.tlu.hrm.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tlu.hrm.entities.LeaveRequest;
import com.tlu.hrm.enums.LeaveStatus;
import com.tlu.hrm.enums.LeaveType;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long>,
JpaSpecificationExecutor<LeaveRequest> {

	// =====================================================
    // Check overlap với đơn APPROVED
    // =====================================================
    @Query("""
        select count(lr) > 0
        from LeaveRequest lr
        where lr.employee.id = :employeeId
          and lr.status = 'APPROVED'
          and lr.startDate <= :endDate
          and lr.endDate >= :startDate
    """)
    boolean existsApprovedOverlap(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // =====================================================
    // Check overlap (exclude current request – update)
    // =====================================================
    @Query("""
        select count(lr) > 0
        from LeaveRequest lr
        where lr.employee.id = :employeeId
          and lr.id <> :excludeId
          and lr.status = 'APPROVED'
          and lr.startDate <= :endDate
          and lr.endDate >= :startDate
    """)
    boolean existsApprovedOverlapExclude(
            @Param("employeeId") Long employeeId,
            @Param("excludeId") Long excludeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // =====================================================
    // Lấy danh sách đơn để tính quota (CREATE)
    // =====================================================
    @Query("""
        select lr
        from LeaveRequest lr
        where lr.employee.id = :employeeId
          and lr.type = :type
          and lr.status in :statuses
    """)
    List<LeaveRequest> findForQuota(
            @Param("employeeId") Long employeeId,
            @Param("type") LeaveType type,
            @Param("statuses") List<LeaveStatus> statuses
    );

    // =====================================================
    // Lấy danh sách đơn để tính quota (UPDATE – exclude)
    // =====================================================
    @Query("""
        select lr
        from LeaveRequest lr
        where lr.employee.id = :employeeId
          and lr.id <> :excludeId
          and lr.type = :type
          and lr.status in :statuses
    """)
    List<LeaveRequest> findForQuotaExclude(
            @Param("employeeId") Long employeeId,
            @Param("excludeId") Long excludeId,
            @Param("type") LeaveType type,
            @Param("statuses") List<LeaveStatus> statuses
    );
    
    List<LeaveRequest> findByEmployeeIdAndStatusIn(Long employeeId, List<LeaveStatus> statuses);
    
    @Query("""
	    SELECT lr
	    FROM LeaveRequest lr
	    WHERE lr.employee.id = :employeeId
	      AND lr.status = 'APPROVED'
	      AND :date BETWEEN lr.startDate AND lr.endDate
	""")
	List<LeaveRequest> findApprovedLeavesForDate(
	    @Param("employeeId") Long employeeId,
	    @Param("date") LocalDate date
	);
}
