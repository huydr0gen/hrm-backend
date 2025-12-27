package com.tlu.hrm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.tlu.hrm.dto.DepartmentApprovalViewDTO;
import com.tlu.hrm.dto.PersonalApprovalViewDTO;
import com.tlu.hrm.enums.ApprovalTargetType;
import com.tlu.hrm.repository.ApprovalConfigRepository;

@Service
public class ApprovalConfigQueryServiceImpl implements ApprovalConfigQueryService {

	private final ApprovalConfigRepository approvalConfigRepository;

	public ApprovalConfigQueryServiceImpl(ApprovalConfigRepository approvalConfigRepository) {
		super();
		this.approvalConfigRepository = approvalConfigRepository;
	}
	
	// =====================================================
    // GET – PHÒNG BAN
    // =====================================================
    @Override
    public Page<DepartmentApprovalViewDTO> getDepartmentApprovals(
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by("createdAt").descending()
        );

        return approvalConfigRepository
            .findByTargetTypeAndActiveTrue(
                ApprovalTargetType.DEPARTMENT,
                pageable
            )
            .map(cfg -> {
                DepartmentApprovalViewDTO dto =
                        new DepartmentApprovalViewDTO();
                dto.setDepartmentId(cfg.getTargetId());
                dto.setApproverId(cfg.getApproverId());
                dto.setCreatedAt(cfg.getCreatedAt());
                return dto;
            });
    }

    // =====================================================
    // GET – CÁ NHÂN
    // =====================================================
    @Override
    public Page<PersonalApprovalViewDTO> getPersonalApprovals(
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by("createdAt").descending()
        );

        return approvalConfigRepository
            .findByTargetTypeAndActiveTrue(
                ApprovalTargetType.EMPLOYEE,
                pageable
            )
            .map(cfg -> {
                PersonalApprovalViewDTO dto =
                        new PersonalApprovalViewDTO();
                dto.setEmployeeId(cfg.getTargetId());
                dto.setApproverId(cfg.getApproverId());
                dto.setCreatedAt(cfg.getCreatedAt());
                return dto;
            });
    }
}
