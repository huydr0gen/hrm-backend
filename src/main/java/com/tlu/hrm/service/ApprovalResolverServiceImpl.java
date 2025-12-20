package com.tlu.hrm.service;

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
}
