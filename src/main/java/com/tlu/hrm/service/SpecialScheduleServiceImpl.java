package com.tlu.hrm.service;

import java.time.Duration;
import java.time.LocalDate;
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
    private final AttendanceCalculationService attendanceCalculationService;

	public SpecialScheduleServiceImpl(SpecialScheduleRepository repository, EmployeeRepository employeeRepository,
			ApprovalResolverService approvalResolverService,
			AttendanceCalculationService attendanceCalculationService) {
		super();
		this.repository = repository;
		this.employeeRepository = employeeRepository;
		this.approvalResolverService = approvalResolverService;
		this.attendanceCalculationService = attendanceCalculationService;
	}

	// ======================================================
    // LIST (SEARCH)
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
    
    @Override
    public Page<SpecialScheduleResponseDTO> getMySchedules(int page, int size) {

        requireRole("ROLE_EMPLOYEE");

        Employee emp = getCurrentEmployee();
        Pageable pageable = PageRequest.of(page, size);

        return repository.findAll(
                (root, query, cb) ->
                        cb.equal(root.get("employee").get("id"), emp.getId()),
                pageable
        ).map(this::toDTO);
    }
    
    @Override
    public Page<SpecialScheduleResponseDTO> getDepartmentSchedules(int page, int size) {

        requireRole("ROLE_MANAGER");

        Employee manager = getCurrentEmployee();
        Long departmentId = manager.getDepartment().getId();
        Pageable pageable = PageRequest.of(page, size);

        return repository.findAll(
                (root, query, cb) ->
                        cb.equal(
                            root.get("employee")
                                .get("department")
                                .get("id"),
                            departmentId
                        ),
                pageable
        ).map(this::toDTO);
    }
    
    @Override
    public Page<SpecialScheduleResponseDTO> getMyApprovalSchedules(int page, int size) {

        Employee actor = getCurrentEmployee();
        Long userId = actor.getUser().getId();
        Pageable pageable = PageRequest.of(page, size);

        return repository.findAll(
                (root, query, cb) ->
                        cb.and(
                            cb.equal(root.get("approverId"), userId),
                            cb.equal(root.get("status"), SpecialScheduleStatus.PENDING)
                        ),
                pageable
        ).map(this::toDTO);
    }

    // ======================================================
    // CREATE
    // ======================================================
    @Override
    public SpecialScheduleResponseDTO create(SpecialScheduleCreateDTO dto) {

        requireRole("ROLE_EMPLOYEE");

        Employee emp = getCurrentEmployee();

        validateDateRange(dto.getStartDate(), dto.getEndDate());

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
                applyWorkingTime(ss, dto);
                validateChildCareWorkingTime(ss);
                ss.setWorkingHours(7);
            }

            case ON_SITE -> {
                validateOnSiteInfo(dto);
                ss.setEndDate(dto.getEndDate());
                applyWorkingTime(ss, dto);
                mapOnSiteInfo(ss, dto);
            }

            case OTHER -> {
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
            throw new IllegalStateException(
                    "Overlapping schedule of same type exists");
        }

        return toDTO(repository.save(ss));
    }

    // ======================================================
    // UPDATE (EMPLOYEE + OWN + PENDING)
    // ======================================================
    @Override
    public SpecialScheduleResponseDTO update(Long id, SpecialScheduleUpdateDTO dto) {

        requireRole("ROLE_EMPLOYEE");

        Employee actor = getCurrentEmployee();
        SpecialSchedule ss = getById(id);

        if (!ss.getEmployee().getId().equals(actor.getId())) {
            throw new AccessDeniedException(
                    "Can only update your own schedule");
        }

        if (ss.getStatus() != SpecialScheduleStatus.PENDING) {
            throw new IllegalStateException(
                    "Only PENDING schedule can be updated");
        }

        validateDateRange(dto.getStartDate(), dto.getEndDate());

        if (ss.getType() != SpecialScheduleType.MATERNITY) {
            ss.setStartDate(dto.getStartDate());
            ss.setEndDate(dto.getEndDate());
            applyWorkingTime(ss, dto);
        }

        if (ss.getType() == SpecialScheduleType.CHILD_CARE) {
            validateChildCareWorkingTime(ss);
        }

        if (ss.getType() == SpecialScheduleType.ON_SITE) {
            mapOnSiteInfo(ss, dto);
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
            throw new IllegalStateException(
                    "Overlapping schedule of same type exists");
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
        
        if (ss.getStatus() == SpecialScheduleStatus.APPROVED) {

            Long employeeId = ss.getEmployee().getId();
            LocalDate start = ss.getStartDate();
            LocalDate end = ss.getEndDate();

            LocalDate d = start;
            while (!d.isAfter(end)) {
                attendanceCalculationService.recalculateDaily(employeeId, d);
                d = d.plusDays(1);
            }
        }

        return toDTO(repository.save(ss));
    }

    // ======================================================
    // BULK DECIDE
    // ======================================================
    @Override
    public BulkDecisionResultDTO decideMany(
            List<Long> ids, DecisionAction action) {

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
    // DETAIL (CHECK PERMISSION)
    // ======================================================
    @Override
    public SpecialScheduleResponseDTO detail(Long id) {

        SpecialSchedule ss = getById(id);
        Employee actor = getCurrentEmployee();
        Set<String> roles = getCurrentRoles();

        if (roles.contains("ROLE_EMPLOYEE")
                && !ss.getEmployee().getId().equals(actor.getId())) {
            throw new AccessDeniedException("Forbidden");
        }

        if (roles.contains("ROLE_MANAGER")
                && !ss.getEmployee().getDepartment().getId()
                        .equals(actor.getDepartment().getId())) {
            throw new AccessDeniedException("Forbidden");
        }

        if (roles.contains("ROLE_ADMIN")) {
            throw new AccessDeniedException("Admin is not allowed");
        }

        return toDTO(ss);
    }

    // ======================================================
    // DELETE (EMPLOYEE + OWN + PENDING)
    // ======================================================
    @Override
    public void delete(Long id) {

        requireRole("ROLE_EMPLOYEE");

        Employee actor = getCurrentEmployee();
        SpecialSchedule ss = getById(id);

        if (!ss.getEmployee().getId().equals(actor.getId())) {
            throw new AccessDeniedException(
                    "Can only delete your own schedule");
        }

        if (ss.getStatus() != SpecialScheduleStatus.PENDING) {
            throw new IllegalStateException(
                    "Only PENDING schedule can be deleted");
        }

        repository.delete(ss);
    }
    
 // ======================================================
 // APPROVER – PENDING LIST (APPROVAL CONFIG)
 // ======================================================
	 @Override
	 public Page<SpecialScheduleResponseDTO> getPendingForApprover(
	         int page,
	         int size) {
	
	     Employee actor = getCurrentEmployee();
	     Long approverEmployeeId = actor.getId();
	
	     Set<Long> approvedEmployeeIds =
	             approvalResolverService.getApprovedEmployeeIds(approverEmployeeId);
	
	     Set<Long> approvedDepartmentIds =
	             approvalResolverService.getApprovedDepartmentIds(approverEmployeeId);
	
	     Pageable pageable = PageRequest.of(page, size);
	
	     Specification<SpecialSchedule> spec =
	             SpecialScheduleSpecification.buildForApprover(
	                     approvedEmployeeIds,
	                     approvedDepartmentIds
	             );
	
	     return repository.findAll(spec, pageable)
	             .map(this::toDTO);
	 }

    // ======================================================
    // WORKING TIME
    // ======================================================
    private void applyWorkingTime(
            SpecialSchedule ss, SpecialScheduleCreateDTO dto) {

        ss.setMorningStart(dto.getMorningStart());
        ss.setMorningEnd(dto.getMorningEnd());
        ss.setAfternoonStart(dto.getAfternoonStart());
        ss.setAfternoonEnd(dto.getAfternoonEnd());
    }

    private void applyWorkingTime(
            SpecialSchedule ss, SpecialScheduleUpdateDTO dto) {

        ss.setMorningStart(dto.getMorningStart());
        ss.setMorningEnd(dto.getMorningEnd());
        ss.setAfternoonStart(dto.getAfternoonStart());
        ss.setAfternoonEnd(dto.getAfternoonEnd());
    }

    // ======================================================
    // VALIDATION
    // ======================================================
    private void validateDateRange(
            LocalDate start, LocalDate end) {

        if (start != null && end != null && end.isBefore(start)) {
            throw new IllegalArgumentException(
                    "End date must not be before start date");
        }
    }

    private void validateChildCareWorkingTime(
            SpecialSchedule ss) {

        long minutes = 0;

        if (ss.getMorningStart() != null
                && ss.getMorningEnd() != null) {
            minutes += Duration
                    .between(ss.getMorningStart(),
                             ss.getMorningEnd())
                    .toMinutes();
        }

        if (ss.getAfternoonStart() != null
                && ss.getAfternoonEnd() != null) {
            minutes += Duration
                    .between(ss.getAfternoonStart(),
                             ss.getAfternoonEnd())
                    .toMinutes();
        }

        if (minutes < 7 * 60) {
            throw new IllegalArgumentException(
                    "Child care schedule must have at least 7 working hours");
        }
    }

    private void validateOnSiteInfo(
            SpecialScheduleCreateDTO dto) {

        if (dto.getProjectCode() == null
                || dto.getProjectName() == null
                || dto.getManagerCode() == null
                || dto.getManagerName() == null) {

            throw new IllegalArgumentException(
                    "ON_SITE schedule requires project and manager information");
        }
    }

    private void mapOnSiteInfo(
            SpecialSchedule ss, SpecialScheduleCreateDTO dto) {

        ss.setProjectCode(dto.getProjectCode());
        ss.setProjectName(dto.getProjectName());
        ss.setOnsiteManagerCode(dto.getManagerCode());
        ss.setOnsiteManagerName(dto.getManagerName());
    }

    private void mapOnSiteInfo(
            SpecialSchedule ss, SpecialScheduleUpdateDTO dto) {

        ss.setProjectCode(dto.getProjectCode());
        ss.setProjectName(dto.getProjectName());
        ss.setOnsiteManagerCode(dto.getManagerCode());
        ss.setOnsiteManagerName(dto.getManagerName());
    }

    // ======================================================
    // HELPERS
    // ======================================================
    private SpecialSchedule getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Special schedule not found"));
    }

    private Employee getCurrentEmployee() {
        Authentication auth =
                SecurityContextHolder.getContext()
                        .getAuthentication();
        CustomUserDetails ud =
                (CustomUserDetails) auth.getPrincipal();

        return employeeRepository
                .findByUserId(ud.getId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Employee not found"));
    }

    private Set<String> getCurrentRoles() {
        Authentication auth =
                SecurityContextHolder.getContext()
                        .getAuthentication();

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

    private SpecialScheduleResponseDTO toDTO(
            SpecialSchedule e) {

        SpecialScheduleResponseDTO dto =
                new SpecialScheduleResponseDTO();

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
