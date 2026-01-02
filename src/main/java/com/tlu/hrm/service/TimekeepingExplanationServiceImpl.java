package com.tlu.hrm.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
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
import com.tlu.hrm.entities.Department;
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
    private final ApprovalResolverService approvalResolverService;
    private final AttendanceCalculationService attendanceCalculationService;
    
	public TimekeepingExplanationServiceImpl(TimekeepingExplanationRepository repository,
			EmployeeRepository employeeRepository, ApprovalResolverService approvalResolverService,
			AttendanceCalculationService attendanceCalculationService) {
		super();
		this.repository = repository;
		this.employeeRepository = employeeRepository;
		this.approvalResolverService = approvalResolverService;
		this.attendanceCalculationService = attendanceCalculationService;
	}

	// =====================================================
    // Helpers
    // =====================================================
    private Employee getCurrentEmployee() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails ud = (CustomUserDetails) auth.getPrincipal();

        return employeeRepository.findByUserId(ud.getId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    private Set<String> getRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet());
    }

    // =====================================================
    // CREATE
    // =====================================================
    @Override
    @Transactional
    public TimekeepingExplanationResponseDTO create(
            TimekeepingExplanationCreateDTO dto) {

        Employee emp = getCurrentEmployee();

        Long approverId = approvalResolverService.resolveApproverId(
                emp.getId(),
                emp.getDepartment().getId()
        );

        TimekeepingExplanation e = new TimekeepingExplanation();
        e.setEmployee(emp);
        e.setWorkDate(dto.getWorkDate());
        e.setProposedCheckIn(dto.getProposedCheckIn());
        e.setProposedCheckOut(dto.getProposedCheckOut());
        e.setReason(dto.getReason());
        e.setStatus(TimekeepingExplanationStatus.PENDING);
        e.setApproverId(approverId);

        if (dto.getWorkDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Không thể giải trình cho ngày tương lai");
        }

        if (dto.getProposedCheckIn() != null && dto.getProposedCheckOut() != null
                && !dto.getProposedCheckIn().isBefore(dto.getProposedCheckOut())) {
            throw new IllegalArgumentException("Giờ vào phải trước giờ ra");
        }
        
        repository.save(e);
        return toDTO(e);
    }

    // =====================================================
    // LIST
    // =====================================================
    @Override
    public Page<TimekeepingExplanationResponseDTO> getList(
            TimekeepingExplanationFilterDTO filter,
            int page,
            int size) {

        Employee actor = getCurrentEmployee();
        Set<String> roles = getRoles();

     // =======================
     // APPROVAL PERMISSIONS
     // =======================
     Long approverEmployeeId = actor.getId();

     Set<Long> approvedEmployeeIds =
             approvalResolverService.getApprovedEmployeeIds(approverEmployeeId);

     Set<Long> approvedDepartmentIds =
             approvalResolverService.getApprovedDepartmentIds(approverEmployeeId);

     // EMPLOYEE thường: chỉ thấy đơn của mình
     if (roles.contains("ROLE_EMPLOYEE")) {
         approvedEmployeeIds = Set.of(actor.getId());
         approvedDepartmentIds = Set.of();
     }

        Pageable pageable = PageRequest.of(
                page, size, Sort.by("createdAt").descending()
        );

        Specification<TimekeepingExplanation> spec =
                TimekeepingExplanationSpecification.buildForApprover(
                        filter,
                        approvedEmployeeIds,
                        approvedDepartmentIds
                );

        return repository.findAll(spec, pageable)
                .map(this::toDTO);
    }

    // =====================================================
    // DETAIL
    // =====================================================
    @Override
    public TimekeepingExplanationResponseDTO getById(Long id) {

        TimekeepingExplanation e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        Employee actor = getCurrentEmployee();
        Set<String> roles = getRoles();

        boolean canView =
                roles.contains("ROLE_HR")
                || e.getEmployee().getId().equals(actor.getId())
                || (roles.contains("ROLE_MANAGER")
                    && e.getEmployee().getDepartment().getId()
                       .equals(actor.getDepartment().getId()))
                || actor.getUser().getId().equals(e.getApproverId());

        if (!canView) {
            throw new AccessDeniedException("No permission");
        }

        return toDTO(e);
    }

    // =====================================================
    // DECIDE ONE (ONLY APPROVER)
    // =====================================================
    @Override
    @Transactional
    public TimekeepingExplanationResponseDTO decide(
            Long id,
            TimekeepingExplanationDecisionDTO dto) {

        TimekeepingExplanation e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        if (e.getStatus() != TimekeepingExplanationStatus.PENDING) {
            throw new IllegalStateException("Already processed");
        }

        Employee actor = getCurrentEmployee();
        Long actorUserId = actor.getUser().getId();

        if (!actorUserId.equals(e.getApproverId())) {
            throw new AccessDeniedException("Not approver");
        }

        e.setStatus(
                dto.getAction() == DecisionAction.APPROVE
                        ? TimekeepingExplanationStatus.APPROVED
                        : TimekeepingExplanationStatus.REJECTED
        );

        e.setDecidedBy(actorUserId);
        e.setDecidedAt(LocalDateTime.now());
        e.setManagerNote(dto.getManagerNote());
        
        if (e.getStatus() == TimekeepingExplanationStatus.APPROVED) {

            attendanceCalculationService.recalculateDaily(
                    e.getEmployee().getId(),
                    e.getWorkDate()
            );
        }

        return toDTO(e);
    }

    // =====================================================
    // DECIDE MANY
    // =====================================================
    @Override
    @Transactional
    public BulkDecisionResultDTO decideMany(BulkDecisionDTO dto) {

        List<Long> success = new ArrayList<>();
        List<Long> failed = new ArrayList<>();

        for (Long id : dto.getIds()) {
            try {
                decide(id,
                        new TimekeepingExplanationDecisionDTO() {{
                            setAction(dto.getAction());
                            setManagerNote(dto.getManagerNote());
                        }});
                success.add(id);
            } catch (Exception e) {
                failed.add(id);
            }
        }

        return new BulkDecisionResultDTO(success, failed);
    }

    // =====================================================
    // Mapper
    // =====================================================
    private TimekeepingExplanationResponseDTO toDTO(TimekeepingExplanation e) {

        TimekeepingExplanationResponseDTO dto =
                new TimekeepingExplanationResponseDTO();

        Employee emp = e.getEmployee();
        Department dept = emp.getDepartment();

        dto.setId(e.getId());
        dto.setEmployeeId(emp.getId());
        dto.setEmployeeCode(emp.getCode());
        dto.setEmployeeName(emp.getFullName());

        dto.setDepartmentId(dept.getId());
        dto.setDepartmentName(dept.getName());

        dto.setWorkDate(e.getWorkDate());
        dto.setOriginalCheckIn(e.getOriginalCheckIn());
        dto.setOriginalCheckOut(e.getOriginalCheckOut());
        dto.setProposedCheckIn(e.getProposedCheckIn());
        dto.setProposedCheckOut(e.getProposedCheckOut());

        dto.setReason(e.getReason());
        dto.setStatus(e.getStatus());

        dto.setApproverId(e.getApproverId());
        dto.setDecidedBy(e.getDecidedBy());
        dto.setDecidedAt(e.getDecidedAt());
        dto.setManagerNote(e.getManagerNote());
        dto.setCreatedAt(e.getCreatedAt());

        return dto;
    }
}
