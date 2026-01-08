package com.tlu.hrm.service;

import java.util.Set;

public interface ApprovalResolverService {

	// ===== DÙNG KHI EMPLOYEE TẠO ĐƠN =====
	Long resolveApproverId(Long employeeId, Long departmentId);

    // ===== DÙNG KHI APPROVER XEM ĐƠN =====
	Set<Long> getApprovedEmployeeIds(Long approverEmployeeId);

	Set<Long> getApprovedDepartmentIds(Long approverEmployeeId);
	
	String resolveApproverCode(String employeeCode, String departmentCode);
	
	boolean hasApprovalPermission(Long approverEmployeeId);
}
