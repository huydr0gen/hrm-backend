package com.tlu.hrm.service;

import org.springframework.data.domain.Page;

import com.tlu.hrm.dto.DepartmentApprovalViewDTO;
import com.tlu.hrm.dto.PersonalApprovalViewDTO;

public interface ApprovalConfigQueryService {

	Page<DepartmentApprovalViewDTO> getDepartmentApprovals(
        int page,
        int size
    );

    Page<PersonalApprovalViewDTO> getPersonalApprovals(
        int page,
        int size
    );
}
