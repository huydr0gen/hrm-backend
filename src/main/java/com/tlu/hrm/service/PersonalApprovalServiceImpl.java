package com.tlu.hrm.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tlu.hrm.dto.PersonalApprovalCreateDTO;
import com.tlu.hrm.dto.PersonalApprovalDecisionDTO;
import com.tlu.hrm.dto.PersonalApprovalResponseDTO;
import com.tlu.hrm.dto.PersonalApprovalUpdateDTO;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.entities.PersonalApprovalRequest;
import com.tlu.hrm.entities.User;
import com.tlu.hrm.enums.ApprovalStatus;
import com.tlu.hrm.repository.PersonalApprovalRequestRepository;
import com.tlu.hrm.repository.UserRepository;
import com.tlu.hrm.security.CustomUserDetails;

@Service
@Transactional
public class PersonalApprovalServiceImpl implements PersonalApprovalService {

	private final PersonalApprovalRequestRepository repository;
    private final UserRepository userRepository;
    
	public PersonalApprovalServiceImpl(PersonalApprovalRequestRepository repository,
			UserRepository userRepository) {
		super();
		this.repository = repository;
		this.userRepository = userRepository;
	}
    
	// =====================================================
    // CREATE PERSONAL APPROVAL
    // =====================================================
    @Override
    public PersonalApprovalResponseDTO create(PersonalApprovalCreateDTO dto) {

        User currentUser = getCurrentUser();
        Employee employee = currentUser.getEmployee();

        if (employee == null) {
            throw new RuntimeException("User is not linked to any employee");
        }

        PersonalApprovalRequest request = new PersonalApprovalRequest(
                employee,
                employee.getDepartment(),
                dto.getType(),
                dto.getReason()
        );

        repository.save(request);

        return mapToResponse(request);
    }
    
 // =====================================================
    // GET MY APPROVALS (PAGINATION)
    // =====================================================
    @Override
    public Page<PersonalApprovalResponseDTO> getMyApprovals(int page, int size) {

        User currentUser = getCurrentUser();
        Employee employee = currentUser.getEmployee();

        if (employee == null) {
            throw new RuntimeException("User is not linked to any employee");
        }

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return repository.findByEmployeeId(employee.getId(), pageable)
                .map(this::mapToResponse);
    }

    // =====================================================
    // GET DEPARTMENT APPROVALS (PAGINATION)
    // =====================================================
    @Override
    public Page<PersonalApprovalResponseDTO> getDepartmentApprovals(int page, int size) {

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

        return repository.findByDepartment(manager.getDepartment(), pageable)
                .map(this::mapToResponse);
    }

    // =====================================================
    // DECIDE (APPROVE / REJECT)
    // =====================================================
    @Override
    public PersonalApprovalResponseDTO decide(Long id, PersonalApprovalDecisionDTO dto) {

        PersonalApprovalRequest request = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Approval request not found"));

        if (request.getStatus() != ApprovalStatus.PENDING) {
            throw new RuntimeException("Only pending requests can be decided");
        }

        User currentUser = getCurrentUser();
        Employee manager = currentUser.getEmployee();

        if (manager == null) {
            throw new RuntimeException("User is not linked to any employee");
        }

        // Manager chỉ được duyệt phòng ban mình
        if (!request.getDepartment().equals(manager.getDepartment())) {
            throw new RuntimeException("You are not allowed to decide this request");
        }

        if (dto.getStatus() != ApprovalStatus.APPROVED
                && dto.getStatus() != ApprovalStatus.REJECTED) {
            throw new RuntimeException("Invalid decision status");
        }

        request.setStatus(dto.getStatus());
        request.setDecisionNote(dto.getNote());
        request.setDecidedBy(currentUser);
        request.setDecidedAt(LocalDateTime.now());

        return mapToResponse(request);
    }
    
	 // =====================================================
	 // UPDATE (ONLY CREATOR & PENDING)
	 // =====================================================
    @Override
    public PersonalApprovalResponseDTO update(Long id, PersonalApprovalUpdateDTO dto) {
    	PersonalApprovalRequest request = repository.findById(id)
    			.orElseThrow(() -> new RuntimeException("Approval request not found"));

    	User currentUser = getCurrentUser();
    	Employee employee = currentUser.getEmployee();

    	if (employee == null) {
    		throw new RuntimeException("User is not linked to any employee");
    	}

    	// Chỉ người tạo mới được sửa
    	if (!request.getEmployee().getId().equals(employee.getId())) {
    		throw new RuntimeException("You can only update your own request");
    	}

    	// Chỉ sửa khi PENDING
    	if (request.getStatus() != ApprovalStatus.PENDING) {
    		throw new RuntimeException("Only pending requests can be updated");
    	}

    	request.setReason(dto.getReason());
    	// updatedAt sẽ tự động set bởi @PreUpdate

    	return mapToResponse(request);
    }

    // =====================================================
    // DELETE (ONLY CREATOR & PENDING)
    // =====================================================
    @Override
    public void delete(Long id) {

        PersonalApprovalRequest request = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Approval request not found"));

        User currentUser = getCurrentUser();

        if (currentUser.getEmployee() == null
                || !request.getEmployee().getId()
                        .equals(currentUser.getEmployee().getId())) {
            throw new RuntimeException("You can only delete your own request");
        }

        if (request.getStatus() != ApprovalStatus.PENDING) {
            throw new RuntimeException("Only pending requests can be deleted");
        }

        repository.delete(request);
    }

    // =====================================================
    // HELPER METHODS
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

    private PersonalApprovalResponseDTO mapToResponse(PersonalApprovalRequest request) {

        PersonalApprovalResponseDTO dto = new PersonalApprovalResponseDTO();

        dto.setId(request.getId());
        dto.setEmployeeId(request.getEmployee().getId());
        dto.setEmployeeName(request.getEmployee().getFullName());
        dto.setDepartment(request.getDepartment());
        dto.setType(request.getType());
        dto.setReason(request.getReason());
        dto.setStatus(request.getStatus());
        dto.setCreatedAt(request.getCreatedAt());

        if (request.getDecidedBy() != null) {
            dto.setDecidedBy(request.getDecidedBy().getUsername());
            dto.setDecidedAt(request.getDecidedAt());
            dto.setDecisionNote(request.getDecisionNote());
        }

        return dto;
    }
}
