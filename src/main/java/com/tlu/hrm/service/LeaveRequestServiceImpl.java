package com.tlu.hrm.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.tlu.hrm.dto.*;
import com.tlu.hrm.entities.*;
import com.tlu.hrm.enums.LeaveStatus;
import com.tlu.hrm.enums.LeaveType;
import com.tlu.hrm.repository.*;
import com.tlu.hrm.spec.LeaveRequestSpecification;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

	private final LeaveRequestRepository repo;
    private final EmployeeRepository employeeRepo;
    private final UserRepository userRepo;
    private final AuditLogService audit;
    
	public LeaveRequestServiceImpl(LeaveRequestRepository repo, EmployeeRepository employeeRepo,
			UserRepository userRepo, AuditLogService audit) {
		super();
		this.repo = repo;
		this.employeeRepo = employeeRepo;
		this.userRepo = userRepo;
		this.audit = audit;
	}
    
	// -----------------------------------------------------
    // CREATE REQUEST
    // -----------------------------------------------------
    @Override
    public LeaveRequestDTO createRequest(LeaveRequestCreateDTO dto) {

        Employee emp = employeeRepo.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LeaveRequest req = new LeaveRequest();
        req.setEmployee(emp);
        req.setType(dto.getType());
        req.setStartDate(dto.getStartDate());
        req.setEndDate(dto.getEndDate());
        req.setReason(dto.getReason());

        LeaveRequest saved = repo.save(req);

        audit.log(emp.getUser() != null ? emp.getUser().getId() : null,
                "LEAVE_CREATE",
                "Created leave request");

        return toDTO(saved);
    }

    // -----------------------------------------------------
    // UPDATE REQUEST (EMPLOYEE ONLY)
    // -----------------------------------------------------
    @Override
    public LeaveRequestDTO updateRequest(Long id, LeaveRequestUpdateDTO dto) {
        LeaveRequest req = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        if (req.getStatus() != LeaveStatus.PENDING)
            throw new RuntimeException("Only pending requests can be updated");

        req.setReason(dto.getReason());
        req.setUpdatedAt(LocalDateTime.now());

        LeaveRequest saved = repo.save(req);

        audit.log(req.getEmployee().getUser().getId(),
                "LEAVE_UPDATE",
                "Updated leave request");

        return toDTO(saved);
    }

    // -----------------------------------------------------
    // DELETE REQUEST (EMPLOYEE ONLY)
    // -----------------------------------------------------
    @Override
    public void deleteRequest(Long id) {
        LeaveRequest req = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        if (req.getStatus() != LeaveStatus.PENDING)
            throw new RuntimeException("Only pending requests can be deleted");

        repo.delete(req);

        audit.log(req.getEmployee().getUser().getId(),
                "LEAVE_DELETE",
                "Deleted leave request");
    }

    // -----------------------------------------------------
    // APPROVE (MANAGER + HR)
    // -----------------------------------------------------
    @Override
    public LeaveRequestDTO approve(Long id, LeaveRequestDecisionDTO dto) {
        LeaveRequest req = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        req.setStatus(LeaveStatus.APPROVED);
        req.setManagerNote(dto.getManagerNote());
        req.setUpdatedAt(LocalDateTime.now());

        LeaveRequest saved = repo.save(req);

        audit.log(req.getEmployee().getUser().getId(),
                "LEAVE_APPROVE",
                "Approved leave request");

        return toDTO(saved);
    }

    // -----------------------------------------------------
    // REJECT (MANAGER + HR)
    // -----------------------------------------------------
    @Override
    public LeaveRequestDTO reject(Long id, LeaveRequestDecisionDTO dto) {
        LeaveRequest req = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        req.setStatus(LeaveStatus.REJECTED);
        req.setManagerNote(dto.getManagerNote());
        req.setUpdatedAt(LocalDateTime.now());

        LeaveRequest saved = repo.save(req);

        audit.log(req.getEmployee().getUser().getId(),
                "LEAVE_REJECT",
                "Rejected leave request");

        return toDTO(saved);
    }

    // -----------------------------------------------------
    // GET BY ID
    // -----------------------------------------------------
    @Override
    public LeaveRequestDTO getById(Long id) {
        LeaveRequest req = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));
        return toDTO(req);
    }

    // -----------------------------------------------------
    // EMPLOYEE: VIEW OWN REQUESTS
    // -----------------------------------------------------
    @Override
    public Page<LeaveRequestDTO> getMyRequests(Long userId, int page, int size) {

        Employee emp = employeeRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<LeaveRequest> data = repo.findAll(
                (root, query, cb) -> cb.equal(root.get("employee"), emp),
                pageable
        );

        return data.map(this::toDTO);
    }

    // -----------------------------------------------------
    // MANAGER: VIEW REQUEST FROM SAME DEPARTMENT
    // -----------------------------------------------------
    @Override
    public Page<LeaveRequestDTO> getDepartmentRequests(Long managerUserId, int page, int size) {

        Employee manager = employeeRepo.findByUserId(managerUserId)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        String department = manager.getDepartment();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<LeaveRequest> spec = LeaveRequestSpecification.hasDepartment(department);

        Page<LeaveRequest> pageData = repo.findAll(spec, pageable);

        return pageData.map(this::toDTO);
    }

    // -----------------------------------------------------
    // HR + ADMIN: FILTERED VIEW (ALL COMPANY)
    // -----------------------------------------------------
    @Override
    public Page<LeaveRequestDTO> getAllFiltered(
            String employeeName,
            String department,
            String status,
            String type,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<LeaveRequest> spec = Specification
                .where(LeaveRequestSpecification.hasEmployeeName(employeeName))
                .and(LeaveRequestSpecification.hasDepartment(department))
                .and(LeaveRequestSpecification.hasStatus(status != null ? LeaveStatus.valueOf(status) : null))
                .and(LeaveRequestSpecification.hasType(type != null ? LeaveType.valueOf(type) : null));

        Page<LeaveRequest> result = repo.findAll(spec, pageable);

        return result.map(this::toDTO);
    }

    // -----------------------------------------------------
    // DTO MAPPER
    // -----------------------------------------------------
    private LeaveRequestDTO toDTO(LeaveRequest req) {
        LeaveRequestDTO dto = new LeaveRequestDTO();

        dto.setId(req.getId());
        dto.setEmployeeId(req.getEmployee().getId());
        dto.setEmployeeName(req.getEmployee().getFullName());
        dto.setDepartment(req.getEmployee().getDepartment());

        dto.setType(req.getType());
        dto.setStartDate(req.getStartDate());
        dto.setEndDate(req.getEndDate());
        dto.setReason(req.getReason());
        dto.setStatus(req.getStatus());
        dto.setManagerNote(req.getManagerNote());

        dto.setCreatedAt(req.getCreatedAt());
        dto.setUpdatedAt(req.getUpdatedAt());

        return dto;
    }
    
}
