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
import com.tlu.hrm.entities.Department;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.entities.SpecialSchedule;
import com.tlu.hrm.enums.DecisionAction;
import com.tlu.hrm.enums.SpecialScheduleStatus;
import com.tlu.hrm.enums.SpecialScheduleType;
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
    private final ApprovalResolverService approvalResolverService;

	public SpecialScheduleServiceImpl(SpecialScheduleRepository repository, EmployeeRepository employeeRepository,
			ApprovalResolverService approvalResolverService) {
		super();
		this.repository = repository;
		this.employeeRepository = employeeRepository;
		this.approvalResolverService = approvalResolverService;
	}

	// ======================================================
    // LIST
    // ======================================================
    @Override
    public Page<SpecialScheduleResponseDTO> list(SpecialScheduleFilterDTO filter) {

        Employee actor = getCurrentEmployee();
        Set<String> roles = getCurrentRoles();

        if (roles.contains("ROLE_EMPLOYEE")) {
            filter.setEmployeeId(actor.getId());
        }

        if (roles.contains("ROLE_MANAGER")) {
            if (filter.getDepartmentId() == null) {
                filter.setDepartmentId(actor.getDepartment().getId());
            }
        }

        Specification<SpecialSchedule> spec =
                SpecialScheduleSpecification.build(filter);

        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize()
        );

        return repository.findAll(spec, pageable)
                .map(this::toDTO);
    }

    // ======================================================
    // CREATE
    // ======================================================
    @Override
    public SpecialScheduleResponseDTO create(SpecialScheduleCreateDTO dto) {

        requireRole("ROLE_EMPLOYEE");

        Employee emp = getCurrentEmployee();

        Long approverId = approvalResolverService.resolveApproverId(
                emp.getId(),
                emp.getDepartment().getId()
        );

        SpecialSchedule ss = new SpecialSchedule();
        ss.setEmployee(emp);
        ss.setStartDate(dto.getStartDate());
        ss.setType(dto.getType());
        ss.setReason(dto.getReason());
        ss.setStatus(SpecialScheduleStatus.PENDING);
        ss.setApproverId(approverId);

        switch (dto.getType()) {

            case MATERNITY -> {
                ss.setEndDate(dto.getStartDate().plusMonths(6));
            }

            case CHILD_CARE -> {
                ss.setEndDate(dto.getStartDate().plusMonths(7));
                ss.setWorkingHours(7); // rule nội bộ
                applyWorkingTime(ss, dto);
            }

            case ON_SITE, OTHER -> {
                ss.setEndDate(dto.getEndDate());
                applyWorkingTime(ss, dto);
            }
        }

        boolean overlap = repository.existsOverlappingSchedule(
                emp,
                ss.getType(),
                List.of(
                        SpecialScheduleStatus.PENDING,
                        SpecialScheduleStatus.APPROVED
                ),
                ss.getStartDate(),
                ss.getEndDate()
        );

        if (overlap) {
            throw new IllegalStateException("Overlapping schedule of same type exists");
        }

        return toDTO(repository.save(ss));
    }

    // ======================================================
    // UPDATE
    // ======================================================
    @Override
    public SpecialScheduleResponseDTO update(Long id, SpecialScheduleUpdateDTO dto) {

        requireRole("ROLE_EMPLOYEE");

        Employee actor = getCurrentEmployee();
        SpecialSchedule ss = getById(id);

        if (!ss.getEmployee().getId().equals(actor.getId())) {
            throw new AccessDeniedException("Can only update your own schedule");
        }

        if (ss.getStatus() != SpecialScheduleStatus.PENDING) {
            throw new IllegalStateException("Only PENDING schedule can be updated");
        }

        if (ss.getType() != SpecialScheduleType.MATERNITY) {
            ss.setStartDate(dto.getStartDate());
            ss.setEndDate(dto.getEndDate());
            applyWorkingTime(ss, dto);
        }

        ss.setReason(dto.getReason());

        boolean overlap = repository.existsOverlappingSchedule(
                actor,
                ss.getType(),
                List.of(
                        SpecialScheduleStatus.PENDING,
                        SpecialScheduleStatus.APPROVED
                ),
                ss.getStartDate(),
                ss.getEndDate()
        );

        if (overlap && !ss.getId().equals(id)) {
            throw new IllegalStateException("Overlapping schedule of same type exists");
        }

        return toDTO(repository.save(ss));
    }

    // ======================================================
    // DECIDE
    // ======================================================
    @Override
    public SpecialScheduleResponseDTO decide(Long id, DecisionAction action) {

        SpecialSchedule ss = getById(id);

        Employee actor = getCurrentEmployee();
        Long actorUserId = actor.getUser().getId();

        if (!actorUserId.equals(ss.getApproverId())) {
            throw new AccessDeniedException("You are not the approver");
        }

        if (ss.getStatus() != SpecialScheduleStatus.PENDING) {
            throw new IllegalStateException("Already processed");
        }

        ss.setStatus(
                action == DecisionAction.APPROVE
                        ? SpecialScheduleStatus.APPROVED
                        : SpecialScheduleStatus.REJECTED
        );

        ss.setDecidedBy(actorUserId);
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

    @Override
    public SpecialScheduleResponseDTO detail(Long id) {
        return toDTO(getById(id));
    }

    // ======================================================
    // WORKING TIME (OVERLOAD – QUAN TRỌNG)
    // ======================================================
    private void applyWorkingTime(SpecialSchedule ss, SpecialScheduleCreateDTO dto) {
        ss.setMorningStart(dto.getMorningStart());
        ss.setMorningEnd(dto.getMorningEnd());
        ss.setAfternoonStart(dto.getAfternoonStart());
        ss.setAfternoonEnd(dto.getAfternoonEnd());
    }

    private void applyWorkingTime(SpecialSchedule ss, SpecialScheduleUpdateDTO dto) {
        ss.setMorningStart(dto.getMorningStart());
        ss.setMorningEnd(dto.getMorningEnd());
        ss.setAfternoonStart(dto.getAfternoonStart());
        ss.setAfternoonEnd(dto.getAfternoonEnd());
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

    private SpecialScheduleResponseDTO toDTO(SpecialSchedule e) {

        SpecialScheduleResponseDTO dto = new SpecialScheduleResponseDTO();

        dto.setId(e.getId());
        dto.setEmployeeId(e.getEmployee().getId());
        dto.setEmployeeCode(e.getEmployee().getCode());
        dto.setEmployeeName(e.getEmployee().getFullName());

        Department dept = e.getEmployee().getDepartment();
        if (dept != null) {
            dto.setDepartmentId(dept.getId());
            dto.setDepartmentName(dept.getName());
        }

        dto.setStartDate(e.getStartDate());
        dto.setEndDate(e.getEndDate());

        dto.setMorningStart(e.getMorningStart());
        dto.setMorningEnd(e.getMorningEnd());
        dto.setAfternoonStart(e.getAfternoonStart());
        dto.setAfternoonEnd(e.getAfternoonEnd());

        dto.setType(e.getType());
        dto.setReason(e.getReason());

        dto.setStatus(e.getStatus());
        dto.setApproverId(e.getApproverId());
        dto.setDecidedBy(e.getDecidedBy());
        dto.setDecidedAt(e.getDecidedAt());
        dto.setCreatedAt(e.getCreatedAt());

        return dto;
    }
}
