package com.tlu.hrm.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tlu.hrm.enums.ApprovalTargetType;
import com.tlu.hrm.repository.ApprovalConfigRepository;

@Service
public class ApprovalResolverServiceImpl implements ApprovalResolverService {

	private final ApprovalConfigRepository repository;

	public ApprovalResolverServiceImpl(ApprovalConfigRepository repository) {
		super();
		this.repository = repository;
	}
	
	// =====================================================
    // DÙNG KHI EMPLOYEE TẠO ĐƠN
    // =====================================================
    @Override
    public Long resolveApproverId(Long employeeId, Long departmentId) {

        // 1️⃣ cá nhân
        var personal = repository
            .findByTargetTypeAndTargetIdAndActiveTrue(
                ApprovalTargetType.EMPLOYEE,
                employeeId
            );

        if (personal.isPresent()) {
            return personal.get().getApproverId();
        }

        // 2️⃣ phòng ban
        var department = repository
            .findByTargetTypeAndTargetIdAndActiveTrue(
                ApprovalTargetType.DEPARTMENT,
                departmentId
            );

        if (department.isPresent()) {
            return department.get().getApproverId();
        }

        throw new RuntimeException(
            "Chưa cấu hình người duyệt cho nhân viên này"
        );
    }

    // =====================================================
    // DÙNG KHI APPROVER XEM ĐƠN CẦN DUYỆT
    // =====================================================

    @Override
    public Set<Long> getApprovedEmployeeIds(Long approverEmployeeId) {

        return repository
            .findAll()
            .stream()
            .filter(c ->
                c.isActive()
                && c.getApproverId().equals(approverEmployeeId)
                && c.getTargetType() == ApprovalTargetType.EMPLOYEE
            )
            .map(c -> c.getTargetId())
            .collect(Collectors.toSet());
    }

    @Override
    public Set<Long> getApprovedDepartmentIds(Long approverEmployeeId) {

        return repository
            .findAll()
            .stream()
            .filter(c ->
                c.isActive()
                && c.getApproverId().equals(approverEmployeeId)
                && c.getTargetType() == ApprovalTargetType.DEPARTMENT
            )
            .map(c -> c.getTargetId())
            .collect(Collectors.toSet());
    }
}
