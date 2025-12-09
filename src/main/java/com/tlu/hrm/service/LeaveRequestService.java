package com.tlu.hrm.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.tlu.hrm.dto.*;

public interface LeaveRequestService {

	LeaveRequestDTO createRequest(LeaveRequestCreateDTO dto);

    LeaveRequestDTO updateRequest(Long id, LeaveRequestUpdateDTO dto);

    void deleteRequest(Long id);   // Employee delete own request

    void delete(Long id);          // HR / ADMIN delete any request

    void deleteMany(List<Long> ids);  // HR / ADMIN delete multiple

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

    void approveMany(List<Long> ids);

    void rejectMany(List<Long> ids);

}
