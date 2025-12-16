package com.tlu.hrm.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tlu.hrm.dto.DepartmentApprovalCreateDTO;
import com.tlu.hrm.dto.DepartmentApprovalDecisionDTO;
import com.tlu.hrm.dto.DepartmentApprovalResponseDTO;
import com.tlu.hrm.entities.DepartmentApprovalRequest;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.entities.User;
import com.tlu.hrm.enums.ApprovalStatus;
import com.tlu.hrm.repository.DepartmentApprovalRequestRepository;
import com.tlu.hrm.repository.UserRepository;
import com.tlu.hrm.security.CustomUserDetails;

@Service
@Transactional
public class DepartmentApprovalServiceImpl implements DepartmentApprovalService {

	private final DepartmentApprovalRequestRepository repository;
    private final UserRepository userRepository;
    
	public DepartmentApprovalServiceImpl(DepartmentApprovalRequestRepository repository,
			UserRepository userRepository) {
		super();
		this.repository = repository;
		this.userRepository = userRepository;
	}
    
	// =====================================================
    // CREATE (MANAGER)
    // =====================================================
    @Override
    public DepartmentApprovalResponseDTO create(DepartmentApprovalCreateDTO dto) {

        User currentUser = getCurrentUser();
        Employee manager = currentUser.getEmployee();

        if (manager == null) {
            throw new RuntimeException("User is not linked to any employee");
        }

        DepartmentApprovalRequest request = new DepartmentApprovalRequest(
                manager.getDepartment(),
                currentUser,
                dto.getType(),
                dto.getContent()
        );

        repository.save(request);

        return mapToResponse(request);
    }

    // =====================================================
    // LIST BY DEPARTMENT (MANAGER)
    // =====================================================
    @Override
    public Page<DepartmentApprovalResponseDTO> getDepartmentApprovals(int page, int size) {

        User currentUser = getCurrentUser();
        Employee manager = currentUser.getEmployee();

        if (manager == null) {
            throw new RuntimeException("User is not linked to any employee");
        }

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return repository
                .findByDepartment(manager.getDepartment(), pageable)
                .map(this::mapToResponse);
    }

    // =====================================================
    // LIST ALL (HR / ADMIN)
    // =====================================================
    @Override
    public Page<DepartmentApprovalResponseDTO> getAllApprovals(int page, int size) {

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return repository
                .findAll(pageable)
                .map(this::mapToResponse);
    }

    // =====================================================
    // DECIDE (HR / ADMIN)
    // =====================================================
    @Override
    public DepartmentApprovalResponseDTO decide(Long id, DepartmentApprovalDecisionDTO dto) {

        DepartmentApprovalRequest request = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department approval request not found"));

        if (request.getStatus() != ApprovalStatus.PENDING) {
            throw new RuntimeException("Only pending requests can be decided");
        }

        if (dto.getStatus() != ApprovalStatus.APPROVED
                && dto.getStatus() != ApprovalStatus.REJECTED) {
            throw new RuntimeException("Invalid decision status");
        }

        User currentUser = getCurrentUser();

        request.setStatus(dto.getStatus());
        request.setDecisionNote(dto.getNote());
        request.setDecidedBy(currentUser);
        request.setDecidedAt(LocalDateTime.now());

        return mapToResponse(request);
    }

    // =====================================================
    // DELETE (CREATOR & PENDING)
    // =====================================================
    @Override
    public void delete(Long id) {

        DepartmentApprovalRequest request = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department approval request not found"));

        User currentUser = getCurrentUser();

        if (!request.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own request");
        }

        if (request.getStatus() != ApprovalStatus.PENDING) {
            throw new RuntimeException("Only pending requests can be deleted");
        }

        repository.delete(request);
    }

    // =====================================================
    // HELPERS
    // =====================================================
    private User getCurrentUser() {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        CustomUserDetails userDetails =
                (CustomUserDetails) auth.getPrincipal();

        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private DepartmentApprovalResponseDTO mapToResponse(DepartmentApprovalRequest request) {

        DepartmentApprovalResponseDTO dto = new DepartmentApprovalResponseDTO();

        dto.setId(request.getId());
        dto.setDepartment(request.getDepartment());
        dto.setType(request.getType());
        dto.setContent(request.getContent());
        dto.setStatus(request.getStatus());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setCreatedBy(request.getCreatedBy().getUsername());

        if (request.getDecidedBy() != null) {
            dto.setDecidedBy(request.getDecidedBy().getUsername());
            dto.setDecidedAt(request.getDecidedAt());
            dto.setDecisionNote(request.getDecisionNote());
        }

        return dto;
    }
}
