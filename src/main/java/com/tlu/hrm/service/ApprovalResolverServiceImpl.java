package com.tlu.hrm.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tlu.hrm.entities.ApprovalConfig;
import com.tlu.hrm.enums.ApprovalTargetType;
import com.tlu.hrm.repository.ApprovalConfigRepository;

@Service
public class ApprovalResolverServiceImpl implements ApprovalResolverService {

	private final ApprovalConfigRepository approvalConfigRepository;

	public ApprovalResolverServiceImpl(ApprovalConfigRepository approvalConfigRepository) {
		super();
		this.approvalConfigRepository = approvalConfigRepository;
	}
	
	// =====================================================
    // CORE – ID BASED (CHO NGHIỆP VỤ)
    // =====================================================

    @Override
    public Long resolveApproverId(Long employeeId, Long departmentId) {

        return approvalConfigRepository
                .findByTargetTypeAndTargetIdAndActiveTrue(
                        ApprovalTargetType.EMPLOYEE,
                        employeeId
                )
                .map(ApprovalConfig::getApproverId)

                .or(() ->
                        approvalConfigRepository
                                .findByTargetTypeAndTargetIdAndActiveTrue(
                                        ApprovalTargetType.DEPARTMENT,
                                        departmentId
                                )
                                .map(ApprovalConfig::getApproverId)
                )

                .orElseThrow(() ->
                        new RuntimeException("Chưa cấu hình người duyệt cho nhân viên này"));
    }

    @Override
    public Set<Long> getApprovedEmployeeIds(Long approverEmployeeId) {

        return approvalConfigRepository
                .findByApproverIdAndActiveTrue(approverEmployeeId)
                .stream()
                .filter(c -> c.getTargetType() == ApprovalTargetType.EMPLOYEE)
                .map(ApprovalConfig::getTargetId)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Long> getApprovedDepartmentIds(Long approverEmployeeId) {

        return approvalConfigRepository
                .findByApproverIdAndActiveTrue(approverEmployeeId)
                .stream()
                .filter(c -> c.getTargetType() == ApprovalTargetType.DEPARTMENT)
                .map(ApprovalConfig::getTargetId)
                .collect(Collectors.toSet());
    }

    // =====================================================
    // CODE BASED – DÙNG CHO UI / CONFIG
    // =====================================================

    @Override
    public String resolveApproverCode(
            String employeeCode,
            String departmentCode
    ) {

        return approvalConfigRepository
                .findByTargetTypeAndTargetCodeAndActiveTrue(
                        ApprovalTargetType.EMPLOYEE,
                        employeeCode
                )
                .map(ApprovalConfig::getApproverCode)

                .or(() ->
                        approvalConfigRepository
                                .findByTargetTypeAndTargetCodeAndActiveTrue(
                                        ApprovalTargetType.DEPARTMENT,
                                        departmentCode
                                )
                                .map(ApprovalConfig::getApproverCode)
                )

                .orElseThrow(() ->
                        new RuntimeException("Chưa cấu hình người duyệt cho nhân viên này"));
    }
    
    @Override
    public boolean hasApprovalPermission(Long approverEmployeeId) {
        return !approvalConfigRepository
                .findByApproverIdAndActiveTrue(approverEmployeeId)
                .isEmpty();
    }
    
}
