package com.tlu.hrm.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.tlu.hrm.dto.BulkDecisionResultDTO;
import com.tlu.hrm.dto.SpecialScheduleCreateDTO;
import com.tlu.hrm.dto.SpecialScheduleFilterDTO;
import com.tlu.hrm.dto.SpecialScheduleResponseDTO;
import com.tlu.hrm.dto.SpecialScheduleUpdateDTO;
import com.tlu.hrm.enums.DecisionAction;

public interface SpecialScheduleService {

	// EMPLOYEE
    Page<SpecialScheduleResponseDTO> getMySchedules(int page, int size);

    // MANAGER
    Page<SpecialScheduleResponseDTO> getDepartmentSchedules(int page, int size);

    // APPROVER
    Page<SpecialScheduleResponseDTO> getMyApprovalSchedules(int page, int size);

    // SEARCH mở rộng
    Page<SpecialScheduleResponseDTO> list(SpecialScheduleFilterDTO filter);

    // CRUD
    SpecialScheduleResponseDTO create(SpecialScheduleCreateDTO dto);

    SpecialScheduleResponseDTO update(Long id, SpecialScheduleUpdateDTO dto);

    void delete(Long id);

    SpecialScheduleResponseDTO detail(Long id);

    // APPROVAL
    SpecialScheduleResponseDTO decide(Long id, DecisionAction action);

    BulkDecisionResultDTO decideMany(List<Long> ids, DecisionAction action);
    
    Page<SpecialScheduleResponseDTO> getPendingForApprover(int page, int size);
}
