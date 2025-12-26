package com.tlu.hrm.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.tlu.hrm.dto.*;
import com.tlu.hrm.entities.Department;
import com.tlu.hrm.enums.DecisionAction;

public interface LeaveRequestService {

	LeaveRequestDTO createRequest(LeaveRequestCreateDTO dto);

    // EMPLOYEE
    LeaveRequestDTO employeeUpdate(Long id, LeaveRequestUpdateDTO dto, Long userId);

    // Read
    LeaveRequestDTO getById(Long id);

    Page<LeaveRequestDTO> getMyRequests(Long userId, int page, int size);

    Page<LeaveRequestDTO> getDepartmentRequests(Long managerId, int page, int size);

    Page<LeaveRequestDTO> getAllFiltered(
        String employeeName,
        Long departmentId,
        String status,
        String type,
        int page,
        int size
    );

    // Decision
    LeaveRequestDTO decide(Long id, DecisionAction action, String comment, Long actorId);

    BulkDecisionResultDTO decideMany(
        List<Long> ids,
        DecisionAction action,
        String comment,
        Long actorId
    );
}
