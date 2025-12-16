package com.tlu.hrm.service;

import org.springframework.data.domain.Page;

import com.tlu.hrm.dto.DepartmentApprovalCreateDTO;
import com.tlu.hrm.dto.DepartmentApprovalDecisionDTO;
import com.tlu.hrm.dto.DepartmentApprovalResponseDTO;

public interface DepartmentApprovalService {

	DepartmentApprovalResponseDTO create(DepartmentApprovalCreateDTO dto);

    Page<DepartmentApprovalResponseDTO> getDepartmentApprovals(int page, int size);

    Page<DepartmentApprovalResponseDTO> getAllApprovals(int page, int size);

    DepartmentApprovalResponseDTO decide(Long id, DepartmentApprovalDecisionDTO dto);

    void delete(Long id);
}
