package com.tlu.hrm.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tlu.hrm.dto.BulkDecisionDTO;
import com.tlu.hrm.dto.BulkDecisionResultDTO;
import com.tlu.hrm.dto.TimekeepingExplanationCreateDTO;
import com.tlu.hrm.dto.TimekeepingExplanationDecisionDTO;
import com.tlu.hrm.dto.TimekeepingExplanationFilterDTO;
import com.tlu.hrm.dto.TimekeepingExplanationResponseDTO;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.entities.TimekeepingExplanation;
import com.tlu.hrm.enums.DecisionAction;
import com.tlu.hrm.enums.TimekeepingExplanationStatus;
import com.tlu.hrm.repository.EmployeeRepository;
import com.tlu.hrm.repository.TimekeepingExplanationRepository;
import com.tlu.hrm.security.CustomUserDetails;
import com.tlu.hrm.spec.TimekeepingExplanationSpecification;

@Service
public class TimekeepingExplanationServiceImpl implements TimekeepingExplanationService {

	private final TimekeepingExplanationRepository repository;
    private final EmployeeRepository employeeRepository;
    
	public TimekeepingExplanationServiceImpl(TimekeepingExplanationRepository repository,
			EmployeeRepository employeeRepository) {
		super();
		this.repository = repository;
		this.employeeRepository = employeeRepository;
	}
    
	// =====================================================
    // Helper: current user
    // =====================================================
    private CustomUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Unauthenticated");
        }

        return (CustomUserDetails) auth.getPrincipal();
    }

    private boolean hasRole(String role) {
        return getCurrentUser().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    private Employee getCurrentEmployee() {
        Long employeeId = getCurrentUser().getEmployeeId();
        if (employeeId == null) {
            return null;
        }

        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    // =====================================================
    // CREATE
    // =====================================================
    @Override
    @Transactional
    public TimekeepingExplanationResponseDTO create(
            TimekeepingExplanationCreateDTO dto
    ) {
        Employee employee = getCurrentEmployee();
        if (employee == null) {
            throw new RuntimeException("Employee not found");
        }

        TimekeepingExplanation entity = new TimekeepingExplanation();
        entity.setEmployee(employee);
        entity.setWorkDate(dto.getWorkDate());
        entity.setProposedCheckIn(dto.getProposedCheckIn());
        entity.setProposedCheckOut(dto.getProposedCheckOut());
        entity.setReason(dto.getReason());
        entity.setStatus(TimekeepingExplanationStatus.PENDING);

        repository.save(entity);

        return mapToResponse(entity);
    }

    // =====================================================
    // LIST + FILTER + PAGING
    // =====================================================
    @Override
    public Page<TimekeepingExplanationResponseDTO> getList(
            TimekeepingExplanationFilterDTO filter,
            int page,
            int size
    ) {
       // CustomUserDetails currentUser = getCurrentUser();
        Employee currentEmployee = getCurrentEmployee();

        String forcedDepartment = null;
        Long forcedEmployeeId = null;

        // MANAGER: chỉ xem phòng ban mình
        if (hasRole("MANAGER")) {
            if (currentEmployee == null) {
                throw new RuntimeException("Manager has no employee info");
            }
            forcedDepartment = currentEmployee.getDepartment();
        }

        // EMPLOYEE: chỉ xem của mình
        if (hasRole("EMPLOYEE")) {
            if (currentEmployee == null) {
                throw new RuntimeException("Employee not found");
            }
            forcedEmployeeId = currentEmployee.getId();
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Specification<TimekeepingExplanation> spec =
                TimekeepingExplanationSpecification.build(
                        filter,
                        forcedDepartment,
                        forcedEmployeeId
                );

        return repository.findAll(spec, pageable)
                .map(this::mapToResponse);
    }

    // =====================================================
    // DETAIL
    // =====================================================
    @Override
    public TimekeepingExplanationResponseDTO getById(Long id) {
        TimekeepingExplanation entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Explanation not found"));

        checkViewPermission(entity.getEmployee());

        return mapToResponse(entity);
    }

    // =====================================================
    // DECIDE ONE
    // =====================================================
    @Override
    @Transactional
    public TimekeepingExplanationResponseDTO decide(
            Long id,
            TimekeepingExplanationDecisionDTO dto
    ) {
        TimekeepingExplanation entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Explanation not found"));

        if (entity.getStatus() != TimekeepingExplanationStatus.PENDING) {
            throw new RuntimeException("Request already decided");
        }

        checkApprovePermission(entity.getEmployee());

        if (dto.getAction() == DecisionAction.APPROVE) {
            entity.setStatus(TimekeepingExplanationStatus.APPROVED);
        } else {
            entity.setStatus(TimekeepingExplanationStatus.REJECTED);
        }

        entity.setDecidedBy(getCurrentUser().getId());
        entity.setDecidedAt(LocalDateTime.now());
        entity.setManagerNote(dto.getManagerNote());

        return mapToResponse(entity);
    }

    // =====================================================
    // DECIDE MANY (NO ERROR DETAIL)
    // =====================================================
    @Override
    @Transactional
    public BulkDecisionResultDTO decideMany(BulkDecisionDTO dto) {

        BulkDecisionResultDTO result = new BulkDecisionResultDTO();
        result.setSuccess(new java.util.ArrayList<>());
        result.setFailed(new java.util.ArrayList<>());

        for (Long id : dto.getIds()) {
            try {
                TimekeepingExplanationDecisionDTO decision =
                        new TimekeepingExplanationDecisionDTO();
                decision.setAction(dto.getAction());
                decision.setManagerNote(dto.getManagerNote());

                decide(id, decision);
                result.getSuccess().add(id);

            } catch (Exception e) {
                result.getFailed().add(id);
            }
        }

        return result;
    }

    // =====================================================
    // Permission checks
    // =====================================================
    private void checkApprovePermission(Employee employee) {

        if (hasRole("HR")) return;

        if (hasRole("MANAGER")) {
            Employee currentEmployee = getCurrentEmployee();
            if (currentEmployee == null) {
                throw new RuntimeException("Employee not found");
            }

            if (!employee.getDepartment()
                    .equals(currentEmployee.getDepartment())) {
                throw new RuntimeException("Not allowed to approve this request");
            }
            return;
        }

        throw new RuntimeException("No permission");
    }

    private void checkViewPermission(Employee employee) {

        if (hasRole("HR")) return;

        Employee currentEmployee = getCurrentEmployee();
        if (currentEmployee == null) {
            throw new RuntimeException("Employee not found");
        }

        if (hasRole("MANAGER")) {
            if (!employee.getDepartment()
                    .equals(currentEmployee.getDepartment())) {
                throw new RuntimeException("Not allowed");
            }
            return;
        }

        if (hasRole("EMPLOYEE")) {
            if (!employee.getId()
                    .equals(currentEmployee.getId())) {
                throw new RuntimeException("Not allowed");
            }
            return;
        }

        throw new RuntimeException("No permission");
    }

    // =====================================================
    // Mapper
    // =====================================================
    private TimekeepingExplanationResponseDTO mapToResponse(
            TimekeepingExplanation e
    ) {
        TimekeepingExplanationResponseDTO dto =
                new TimekeepingExplanationResponseDTO();

        dto.setId(e.getId());

        Employee emp = e.getEmployee();
        dto.setEmployeeId(emp.getId());
        dto.setEmployeeCode(emp.getCode());
        dto.setEmployeeName(emp.getFullName());
        dto.setDepartment(emp.getDepartment());

        dto.setWorkDate(e.getWorkDate());
        dto.setOriginalCheckIn(e.getOriginalCheckIn());
        dto.setOriginalCheckOut(e.getOriginalCheckOut());
        dto.setProposedCheckIn(e.getProposedCheckIn());
        dto.setProposedCheckOut(e.getProposedCheckOut());

        dto.setReason(e.getReason());
        dto.setStatus(e.getStatus());

        dto.setDecidedBy(e.getDecidedBy());
        dto.setDecidedAt(e.getDecidedAt());
        dto.setManagerNote(e.getManagerNote());

        dto.setCreatedAt(e.getCreatedAt());

        return dto;
    }
}
