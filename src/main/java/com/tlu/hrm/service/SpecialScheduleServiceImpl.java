package com.tlu.hrm.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tlu.hrm.dto.BulkDecisionResultDTO;
import com.tlu.hrm.dto.SpecialScheduleCreateDTO;
import com.tlu.hrm.dto.SpecialScheduleFilterDTO;
import com.tlu.hrm.dto.SpecialScheduleResponseDTO;
import com.tlu.hrm.dto.SpecialScheduleUpdateDTO;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.entities.SpecialSchedule;
import com.tlu.hrm.enums.DecisionAction;
import com.tlu.hrm.enums.SpecialScheduleStatus;
import com.tlu.hrm.repository.EmployeeRepository;
import com.tlu.hrm.repository.SpecialScheduleRepository;
import com.tlu.hrm.security.CustomUserDetails;
import com.tlu.hrm.spec.SpecialScheduleSpecification;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SpecialScheduleServiceImpl implements SpecialScheduleService {

	private final SpecialScheduleRepository repository;
    private final EmployeeRepository employeeRepository;

	public SpecialScheduleServiceImpl(SpecialScheduleRepository repository, EmployeeRepository employeeRepository) {
		super();
		this.repository = repository;
		this.employeeRepository = employeeRepository;
	}

	// ======================================================
    // LIST
    // ======================================================
    @Override
    public Page<SpecialScheduleResponseDTO> list(SpecialScheduleFilterDTO filter) {

        Employee actor = getCurrentEmployee();
        Set<String> roles = getCurrentRoles();

        // EMPLOYEE: chỉ xem của chính mình
        if (roles.contains("ROLE_EMPLOYEE")) {
            filter.setEmployeeId(actor.getId());
        }

        // MANAGER: chỉ xem trong phòng ban
        if (roles.contains("ROLE_MANAGER") && !isHRorAdmin(roles)) {
            List<Long> empIds = employeeRepository
                    .findByDepartment(actor.getDepartment())
                    .stream()
                    .map(Employee::getId)
                    .toList();

            filter.setEmployeeIds(empIds);
        }

        Specification<SpecialSchedule> spec =
                SpecialScheduleSpecification.build(filter);

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());

        return repository.findAll(spec, pageable)
                .map(this::toDTO);
    }

    // ======================================================
    // CREATE – EMPLOYEE
    // ======================================================
    @Override
    public SpecialScheduleResponseDTO create(SpecialScheduleCreateDTO dto) {

        requireRole("ROLE_EMPLOYEE");

        Employee emp = getCurrentEmployee();

        SpecialSchedule ss = new SpecialSchedule();
        ss.setEmployee(emp);
        ss.setDate(dto.getDate());
        ss.setReason(dto.getReason());
        ss.setStatus(SpecialScheduleStatus.PENDING);

        return toDTO(repository.save(ss));
    }

    // ======================================================
    // UPDATE – HR / ADMIN (PENDING ONLY)
    // ======================================================
    @Override
    public SpecialScheduleResponseDTO update(Long id, SpecialScheduleUpdateDTO dto) {

        requireHRorAdmin();

        SpecialSchedule ss = getById(id);

        if (ss.getStatus() != SpecialScheduleStatus.PENDING) {
            throw new IllegalStateException("Only PENDING schedule can be updated");
        }

        ss.setDate(dto.getDate());
        ss.setReason(dto.getReason());
        ss.setUpdatedAt(LocalDateTime.now());

        return toDTO(repository.save(ss));
    }

    // ======================================================
    // DECIDE – HR / MANAGER
    // ======================================================
    @Override
    public SpecialScheduleResponseDTO decide(Long id, DecisionAction action) {

        Employee actor = getCurrentEmployee();
        Set<String> roles = getCurrentRoles();

        SpecialSchedule ss = getById(id);

        if (ss.getStatus() != SpecialScheduleStatus.PENDING) {
            throw new IllegalStateException("Already processed");
        }

        // MANAGER chỉ được quyết trong phòng ban
        if (roles.contains("ROLE_MANAGER") && !isHRorAdmin(roles)) {
            if (!actor.getDepartment()
                    .equals(ss.getEmployee().getDepartment())) {
                throw new AccessDeniedException("Out of department");
            }
        }

        // HR / ADMIN thì ok

        ss.setStatus(
                action == DecisionAction.APPROVE
                        ? SpecialScheduleStatus.APPROVED
                        : SpecialScheduleStatus.REJECTED
        );

        ss.setDecidedBy(actor.getUser().getId());
        ss.setDecidedAt(LocalDateTime.now());

        return toDTO(repository.save(ss));
    }

    // ======================================================
    // BULK DECIDE
    // ======================================================
    @Override
    public BulkDecisionResultDTO decideMany(List<Long> ids, DecisionAction action) {

        List<Long> success = new ArrayList<>();
        List<Long> failed = new ArrayList<>();

        for (Long id : ids) {
            try {
                decide(id, action);
                success.add(id);
            } catch (Exception e) {
                failed.add(id);
            }
        }

        return new BulkDecisionResultDTO(success, failed);
    }

    // ======================================================
    // DETAIL
    // ======================================================
    @Override
    public SpecialScheduleResponseDTO detail(Long id) {
        return toDTO(getById(id));
    }

    // ======================================================
    // HELPERS
    // ======================================================
    private SpecialSchedule getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Special schedule not found"));
    }

    private Employee getCurrentEmployee() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails ud = (CustomUserDetails) auth.getPrincipal();

        return employeeRepository.findByUserId(ud.getId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    private Set<String> getCurrentRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet());
    }

    private void requireRole(String role) {
        if (!getCurrentRoles().contains(role)) {
            throw new AccessDeniedException("Forbidden");
        }
    }

    private void requireHRorAdmin() {
        Set<String> roles = getCurrentRoles();
        if (!isHRorAdmin(roles)) {
            throw new AccessDeniedException("Only HR/Admin allowed");
        }
    }

    private boolean isHRorAdmin(Set<String> roles) {
        return roles.contains("ROLE_HR") || roles.contains("ROLE_ADMIN");
    }

    private SpecialScheduleResponseDTO toDTO(SpecialSchedule e) {
        SpecialScheduleResponseDTO dto = new SpecialScheduleResponseDTO();

        dto.setId(e.getId());
        dto.setEmployeeId(e.getEmployee().getId());
        dto.setEmployeeName(e.getEmployee().getFullName());
        dto.setDepartment(e.getEmployee().getDepartment());
        dto.setDate(e.getDate());
        dto.setReason(e.getReason());
        dto.setStatus(e.getStatus());
        dto.setDecidedBy(e.getDecidedBy());
        dto.setDecidedAt(e.getDecidedAt());
        dto.setCreatedAt(e.getCreatedAt());

        return dto;
    }
}
