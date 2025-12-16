package com.tlu.hrm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tlu.hrm.entities.PersonalApprovalRequest;
import com.tlu.hrm.enums.ApprovalStatus;

public interface PersonalApprovalRequestRepository extends JpaRepository<PersonalApprovalRequest, Long> {
    
 // Nhân viên xem yêu cầu của mình (phân trang)
    Page<PersonalApprovalRequest> findByEmployeeId(
            Long employeeId,
            Pageable pageable
    );

    // Quản lý xem yêu cầu theo phòng ban (phân trang)
    Page<PersonalApprovalRequest> findByDepartment(
            String department,
            Pageable pageable
    );

    // Quản lý lọc theo phòng ban + trạng thái (phân trang)
    Page<PersonalApprovalRequest> findByDepartmentAndStatus(
            String department,
            ApprovalStatus status,
            Pageable pageable
    );
}
