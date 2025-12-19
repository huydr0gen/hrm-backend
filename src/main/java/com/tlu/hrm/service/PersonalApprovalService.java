package com.tlu.hrm.service;

import org.springframework.data.domain.Page;

import com.tlu.hrm.dto.PersonalApprovalCreateDTO;
import com.tlu.hrm.dto.PersonalApprovalDecisionDTO;
import com.tlu.hrm.dto.PersonalApprovalResponseDTO;
import com.tlu.hrm.dto.PersonalApprovalUpdateDTO;

public interface PersonalApprovalService {

	PersonalApprovalResponseDTO create(PersonalApprovalCreateDTO dto);

    Page<PersonalApprovalResponseDTO> getMyApprovals(int page, int size);

    Page<PersonalApprovalResponseDTO> getDepartmentApprovals(int page, int size);

    PersonalApprovalResponseDTO decide(Long id, PersonalApprovalDecisionDTO dto);

    PersonalApprovalResponseDTO update(Long id, PersonalApprovalUpdateDTO dto);

    void delete(Long id);
}
