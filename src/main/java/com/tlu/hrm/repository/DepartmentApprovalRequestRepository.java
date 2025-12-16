package com.tlu.hrm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tlu.hrm.entities.DepartmentApprovalRequest;
import com.tlu.hrm.enums.ApprovalStatus;

public interface DepartmentApprovalRequestRepository extends JpaRepository<DepartmentApprovalRequest, Long> {

	// Manager xem các yêu cầu của phòng ban mình (phân trang)
    Page<DepartmentApprovalRequest> findByDepartment(
            String department,
            Pageable pageable
    );

    // Manager / HR lọc theo phòng ban + trạng thái (phân trang)
    Page<DepartmentApprovalRequest> findByDepartmentAndStatus(
            String department,
            ApprovalStatus status,
            Pageable pageable
    );

    // HR / Admin xem tất cả theo trạng thái (phân trang)
    Page<DepartmentApprovalRequest> findByStatus(
            ApprovalStatus status,
            Pageable pageable
    );
}
