package com.tlu.hrm.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tlu.hrm.dto.*;
import com.tlu.hrm.entities.*;
import com.tlu.hrm.enums.DecisionAction;
import com.tlu.hrm.enums.LeaveStatus;
import com.tlu.hrm.enums.LeaveType;
import com.tlu.hrm.enums.NotificationType;
import com.tlu.hrm.repository.*;
import com.tlu.hrm.security.CustomUserDetails;
import com.tlu.hrm.spec.LeaveRequestSpecification;

import jakarta.transaction.Transactional;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

	private static final int ANNUAL_LEAVE_QUOTA = 12;
	
	private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepo;
    private final UserRepository userRepo;
    private final AuditLogService audit;
    private final ApprovalResolverService approvalResolverService;
    private final AttendanceCalculationService attendanceCalculationService;
    private final NotificationService notificationService;
    
	public LeaveRequestServiceImpl(LeaveRequestRepository leaveRequestRepository, EmployeeRepository employeeRepo,
			UserRepository userRepo, AuditLogService audit, ApprovalResolverService approvalResolverService,
			AttendanceCalculationService attendanceCalculationService, NotificationService notificationService) {
		super();
		this.leaveRequestRepository = leaveRequestRepository;
		this.employeeRepo = employeeRepo;
		this.userRepo = userRepo;
		this.audit = audit;
		this.approvalResolverService = approvalResolverService;
		this.attendanceCalculationService = attendanceCalculationService;
		this.notificationService = notificationService;
	}

	// =====================================================
    // Helper
    // =====================================================

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Unauthenticated");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails ud) {
            return ud.getId();
        }

        if (principal instanceof String username) {
            return userRepo.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId();
        }

        throw new RuntimeException("Cannot resolve current user id");
    }

    private int calcDays(LocalDate start, LocalDate end) {
        return (int) ChronoUnit.DAYS.between(start, end) + 1;
    }

    // =====================================================
    // CREATE (EMPLOYEE)
    // =====================================================

    @Override
    @Transactional
    public LeaveRequestDTO createRequest(LeaveRequestCreateDTO dto) {

        Long userId = getCurrentUserId();

        Employee emp = employeeRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        boolean overlap = leaveRequestRepository.existsApprovedOverlap(
                emp.getId(),
                dto.getStartDate(),
                dto.getEndDate()
        );
        if (overlap) {
            throw new IllegalStateException("Leave overlaps with approved request");
        }

        if (dto.getType() == LeaveType.ANNUAL) {

            int usedDays = leaveRequestRepository
                    .findForQuota(
                            emp.getId(),
                            LeaveType.ANNUAL,
                            List.of(LeaveStatus.APPROVED, LeaveStatus.PENDING)
                    )
                    .stream()
                    .mapToInt(lr -> calcDays(lr.getStartDate(), lr.getEndDate()))
                    .sum();

            int newDays = calcDays(dto.getStartDate(), dto.getEndDate());

            if (usedDays + newDays > ANNUAL_LEAVE_QUOTA) {
                throw new IllegalStateException("Exceed annual leave quota");
            }
        }

        Long approverId = approvalResolverService.resolveApproverId(
                emp.getId(),
                emp.getDepartment().getId()
        );

        LeaveRequest req = new LeaveRequest();
        req.setEmployee(emp);
        req.setType(dto.getType());
        req.setStartDate(dto.getStartDate());
        req.setEndDate(dto.getEndDate());
        req.setReason(dto.getReason());
        req.setStatus(LeaveStatus.PENDING);
        req.setApproverId(approverId);
        req.setCreatedAt(LocalDateTime.now());

        LeaveRequest saved = leaveRequestRepository.save(req);
        
        notificationService.createNotification(
                saved.getApproverId(), // người duyệt
                "Có đơn nghỉ phép mới",
                "Nhân viên " + emp.getFullName()
                        + " đã gửi đơn nghỉ phép từ "
                        + saved.getStartDate()
                        + " đến "
                        + saved.getEndDate(),
                NotificationType.LEAVE_REQUEST
        );

        audit.log(userId, "LEAVE_CREATE",
                "Create leave request id=" + saved.getId());

        return toDTO(saved);
    }

    // =====================================================
    // UPDATE (EMPLOYEE)
    // =====================================================

    @Override
    @Transactional
    public LeaveRequestDTO employeeUpdate(Long id, LeaveRequestUpdateDTO dto, Long userId) {

        LeaveRequest req = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        if (req.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Only PENDING request can be updated");
        }

        Employee emp = employeeRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (!req.getEmployee().getId().equals(emp.getId())) {
            throw new SecurityException("You can only update your own request");
        }

        if (dto.getStartDate() != null) req.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) req.setEndDate(dto.getEndDate());
        if (dto.getReason() != null) req.setReason(dto.getReason());

        if (req.getStartDate().isAfter(req.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        boolean overlap = leaveRequestRepository.existsApprovedOverlapExclude(
                emp.getId(),
                req.getId(),
                req.getStartDate(),
                req.getEndDate()
        );
        if (overlap) {
            throw new IllegalStateException("Leave overlaps with approved request");
        }

        if (req.getType() == LeaveType.ANNUAL) {

            int usedDays = leaveRequestRepository
                    .findForQuotaExclude(
                            emp.getId(),
                            req.getId(),
                            LeaveType.ANNUAL,
                            List.of(LeaveStatus.APPROVED, LeaveStatus.PENDING)
                    )
                    .stream()
                    .mapToInt(lr -> calcDays(lr.getStartDate(), lr.getEndDate()))
                    .sum();

            int newDays = calcDays(req.getStartDate(), req.getEndDate());

            if (usedDays + newDays > ANNUAL_LEAVE_QUOTA) {
                throw new IllegalStateException("Exceed annual leave quota");
            }
        }

        req.setUpdatedAt(LocalDateTime.now());

        LeaveRequest saved = leaveRequestRepository.save(req);

        audit.log(userId, "LEAVE_UPDATE",
                "Update leave request id=" + id);

        return toDTO(saved);
    }

    // =====================================================
    // GET / FILTER
    // =====================================================

    @Override
    public LeaveRequestDTO getById(Long id) {
        return leaveRequestRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));
    }

    @Override
    public Page<LeaveRequestDTO> getMyRequests(Long userId, int page, int size) {

        Employee emp = employeeRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return leaveRequestRepository
                .findAll(
                        (root, q, cb) -> cb.equal(root.get("employee"), emp),
                        pageable
                )
                .map(this::toDTO);
    }

    @Override
    public Page<LeaveRequestDTO> getAllFiltered(
            String employeeName,
            Long departmentId,
            String status,
            String type,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<LeaveRequest> spec = Specification
                .where(LeaveRequestSpecification.hasEmployeeName(employeeName))
                .and(LeaveRequestSpecification.hasDepartment(departmentId))
                .and(LeaveRequestSpecification.hasStatus(
                        status != null ? LeaveStatus.valueOf(status) : null))
                .and(LeaveRequestSpecification.hasType(
                        type != null ? LeaveType.valueOf(type) : null));

        return leaveRequestRepository.findAll(spec, pageable)
                .map(this::toDTO);
    }

    // =====================================================
    // DECIDE
    // =====================================================

    @Override
    @Transactional
    public LeaveRequestDTO decide(Long id, DecisionAction action, String note, Long actorId) {

        LeaveRequest req = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        if (req.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Request already processed");
        }

        if (!req.getApproverId().equals(actorId)) {
            throw new SecurityException("You are not assigned approver");
        }

        req.setStatus(
                action == DecisionAction.APPROVE
                        ? LeaveStatus.APPROVED
                        : LeaveStatus.REJECTED
        );

        req.setManagerNote(note);
        req.setDecidedBy(actorId);
        req.setDecidedAt(LocalDateTime.now());
        req.setUpdatedAt(LocalDateTime.now());

        LeaveRequest saved = leaveRequestRepository.save(req);
        
        notificationService.createNotification(
                saved.getEmployee().getId(), // nhân viên gửi đơn
                action == DecisionAction.APPROVE
                        ? "Đơn nghỉ phép đã được duyệt"
                        : "Đơn nghỉ phép bị từ chối",
                action == DecisionAction.APPROVE
                        ? "Đơn nghỉ phép từ " + saved.getStartDate()
                                + " đến " + saved.getEndDate() + " đã được duyệt"
                        : "Đơn nghỉ phép từ " + saved.getStartDate()
                                + " đến " + saved.getEndDate() + " đã bị từ chối",
                action == DecisionAction.APPROVE
                        ? NotificationType.LEAVE_APPROVED
                        : NotificationType.LEAVE_REJECTED
        );

        if (saved.getStatus() == LeaveStatus.APPROVED) {
            LocalDate d = saved.getStartDate();
            while (!d.isAfter(saved.getEndDate())) {
                attendanceCalculationService.recalculateDaily(
                        saved.getEmployee().getId(), d);
                d = d.plusDays(1);
            }
        }

        audit.log(actorId, "LEAVE_DECIDE",
                action.name() + " leave request id=" + id);

        return toDTO(saved);
    }

    @Override
    @Transactional
    public BulkDecisionResultDTO decideMany(
            List<Long> ids,
            DecisionAction action,
            String note,
            Long actorId
    ) {

        List<Long> success = new ArrayList<>();
        List<Long> failed = new ArrayList<>();

        for (Long id : ids) {
            try {
                decide(id, action, note, actorId);
                success.add(id);
            } catch (Exception ex) {
                failed.add(id);
            }
        }

        audit.log(actorId, "LEAVE_DECIDE_BULK",
                action.name() + " leaveRequestIds=" + ids);

        return new BulkDecisionResultDTO(success, failed);
    }

    // =====================================================
    // APPROVER – PENDING LIST
    // =====================================================

    @Override
    public Page<LeaveRequestDTO> getPendingForApprover(int page, int size) {

        Long userId = getCurrentUserId();

        Employee actor = employeeRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Long approverEmployeeId = actor.getId();

        Set<Long> approvedEmployeeIds =
                approvalResolverService.getApprovedEmployeeIds(approverEmployeeId);

        Set<Long> approvedDepartmentIds =
                approvalResolverService.getApprovedDepartmentIds(approverEmployeeId);

        Pageable pageable =
                PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<LeaveRequest> spec =
                LeaveRequestSpecification.buildForApprover(
                        approvedEmployeeIds,
                        approvedDepartmentIds
                );

        return leaveRequestRepository.findAll(spec, pageable)
                .map(this::toDTO);
    }

    // =====================================================
    // DTO MAPPER
    // =====================================================

    private LeaveRequestDTO toDTO(LeaveRequest req) {

        LeaveRequestDTO dto = new LeaveRequestDTO();
        dto.setId(req.getId());
        dto.setEmployeeId(req.getEmployee().getId());
        dto.setEmployeeCode(req.getEmployee().getCode());
        dto.setEmployeeName(req.getEmployee().getFullName());

        dto.setDepartmentId(req.getEmployee().getDepartment().getId());
        dto.setDepartmentName(req.getEmployee().getDepartment().getName());

        dto.setApproverId(req.getApproverId());
        dto.setType(req.getType());
        dto.setStartDate(req.getStartDate());
        dto.setEndDate(req.getEndDate());
        dto.setReason(req.getReason());
        dto.setStatus(req.getStatus());
        dto.setManagerNote(req.getManagerNote());
        dto.setDecidedBy(req.getDecidedBy());
        dto.setDecidedAt(req.getDecidedAt());
        dto.setCreatedAt(req.getCreatedAt());
        dto.setUpdatedAt(req.getUpdatedAt());

        return dto;
    }
}
