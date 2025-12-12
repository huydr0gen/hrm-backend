package com.tlu.hrm.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.tlu.hrm.dto.*;
import com.tlu.hrm.enums.DecisionAction;

public interface LeaveRequestService {

	LeaveRequestDTO createRequest(LeaveRequestCreateDTO dto);

    // HR / ADMIN: update (admin edit)
    LeaveRequestDTO adminUpdate(Long id, LeaveRequestUpdateDTO dto, Long actorId);

    // HR / ADMIN: delete operations
    void delete(Long id);                  // HR / ADMIN delete any request
    void deleteMany(List<Long> ids);       // HR / ADMIN delete multiple requests

    // Read
    LeaveRequestDTO getById(Long id);

    Page<LeaveRequestDTO> getMyRequests(Long userId, int page, int size);

    Page<LeaveRequestDTO> getDepartmentRequests(Long managerId, int page, int size);

    Page<LeaveRequestDTO> getAllFiltered(
            String employeeName,
            String department,
            String status,
            String type,
            int page,
            int size);

    // Unified decision API (approve/reject via enum)
    LeaveRequestDTO decide(Long id, DecisionAction action, String comment, Long actorId);

    BulkDecisionResultDTO decideMany(List<Long> ids, DecisionAction action, String comment, Long actorId);
}
