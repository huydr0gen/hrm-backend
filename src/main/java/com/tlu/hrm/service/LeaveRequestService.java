package com.tlu.hrm.service;

import org.springframework.data.domain.Page;

import com.tlu.hrm.dto.*;

public interface LeaveRequestService {

	LeaveRequestDTO createRequest(LeaveRequestCreateDTO dto);

    LeaveRequestDTO updateRequest(Long id, LeaveRequestUpdateDTO dto);

    void deleteRequest(Long id);

    LeaveRequestDTO approve(Long id, LeaveRequestDecisionDTO dto);

    LeaveRequestDTO reject(Long id, LeaveRequestDecisionDTO dto);

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
}
