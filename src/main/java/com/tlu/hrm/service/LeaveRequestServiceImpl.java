package com.tlu.hrm.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.tlu.hrm.enums.LeaveDuration;
import com.tlu.hrm.enums.LeaveStatus;
import com.tlu.hrm.enums.LeaveType;
import com.tlu.hrm.enums.NotificationType;
import com.tlu.hrm.repository.*;
import com.tlu.hrm.security.CustomUserDetails;
import com.tlu.hrm.spec.LeaveRequestSpecification;

import jakarta.transaction.Transactional;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {
	
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

    private double getDurationValue(LeaveDuration duration) {
        if (duration == null) return 1.0;
        return switch (duration) {
            case FULL_DAY -> 1.0;
            case MORNING, AFTERNOON -> 0.5;
        };
    }

    private boolean hasOverlap(Long employeeId, Long excludeId, LocalDate leaveDate) {
        if (excludeId == null) {
            return leaveRequestRepository.existsApprovedOverlap(employeeId, leaveDate);
        }
        return leaveRequestRepository.existsApprovedOverlapExclude(employeeId, excludeId, leaveDate);
    }

    private double calculateUsedQuota(Long employeeId, Long excludeId, int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<LeaveRequest> list;

        if (excludeId == null) {
            list = leaveRequestRepository.findForQuotaByYear(
                employeeId,
                LeaveType.ANNUAL,
                List.of(LeaveStatus.APPROVED, LeaveStatus.PENDING),
                startDate,
                endDate
            );
        } else {
            list = leaveRequestRepository.findForQuotaExcludeByYear(
                employeeId,
                excludeId,
                LeaveType.ANNUAL,
                List.of(LeaveStatus.APPROVED, LeaveStatus.PENDING),
                startDate,
                endDate
            );
        }

        return list.stream()
                .mapToDouble(lr -> getDurationValue(lr.getDuration()))
                .sum();
    }
    
    private double getCurrentQuota(Employee emp, LocalDate asOfDate) {
        int year = asOfDate.getYear();

        LocalDate onboard = emp.getOnboardDate();
        LocalDate yearStart = LocalDate.of(year, 1, 1);
        LocalDate yearEnd = LocalDate.of(year, 12, 31);

        if (onboard.isAfter(yearEnd)) {
            return 0;
        }

        LocalDate onboardStart = normalizeOnboardStart(onboard);
        LocalDate effectiveStart = onboardStart.isAfter(yearStart) ? onboardStart : yearStart;

        return countMonthsInclusive(effectiveStart, asOfDate);
    }
    
    private LocalDate normalizeOnboardStart(LocalDate onboardDate) {
        if (onboardDate.getDayOfMonth() == 1) {
            return onboardDate.withDayOfMonth(1);
        } else {
            return onboardDate.plusMonths(1).withDayOfMonth(1);
        }
    }
    
    private int countMonthsInclusive(LocalDate start, LocalDate end) {
        LocalDate s = start.withDayOfMonth(1);
        LocalDate e = end.withDayOfMonth(1);

        if (s.isAfter(e)) return 0;

        return (e.getYear() - s.getYear()) * 12
             + (e.getMonthValue() - s.getMonthValue()) + 1;
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

        if (dto.getLeaveDate().isBefore(emp.getOnboardDate())) {
            throw new RuntimeException("Không thể xin nghỉ trước ngày onboard");
        }

        boolean overlap = hasOverlap(
                emp.getId(),
                null,
                dto.getLeaveDate()
        );
        if (overlap) {
            throw new IllegalStateException("Đơn nghỉ bị trùng ngày");
        }

        if (dto.getType() == LeaveType.ANNUAL) {
            double newAmount = getDurationValue(dto.getDuration());

            LocalDate leaveDate = dto.getLeaveDate();
            int year = leaveDate.getYear();

            double quota = getCurrentQuota(emp, leaveDate);
            double used = calculateUsedQuota(emp.getId(), null, year);

            if (used + newAmount > quota) {
            	throw new IllegalStateException(
            		    "Không đủ ngày phép. Còn lại: " + (quota - used)
            		);
            }
        }

        Long approverId = approvalResolverService.resolveApproverId(
                emp.getId(),
                emp.getDepartment().getId()
        );
        
        if (approverId == null || approverId.equals(emp.getId())) {
            throw new RuntimeException(
                "Bạn không thể tự duyệt đơn của chính mình. Vui lòng liên hệ HR/Admin để được gán người duyệt."
            );
        }

        LeaveRequest req = new LeaveRequest();
        req.setEmployee(emp);
        req.setType(dto.getType());
        req.setDuration(dto.getDuration());
        req.setLeaveDate(dto.getLeaveDate());
        req.setReason(dto.getReason());
        req.setStatus(LeaveStatus.PENDING);
        req.setApproverId(approverId);

        LeaveRequest saved = leaveRequestRepository.save(req);

        notificationService.createNotification(
                saved.getApproverId(),
                "Có đơn nghỉ phép mới",
                "Nhân viên " + emp.getFullName()
                        + " đã gửi đơn nghỉ phép ngày "
                        + saved.getLeaveDate(),
                NotificationType.LEAVE_REQUEST
        );

        audit.log(userId, "LEAVE_CREATE",
                "Tạo đơn nghỉ phép, mã đơn = " + saved.getId());

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
            throw new IllegalStateException("Chỉ được sửa đơn khi đang ở trạng thái PENDING");
        }

        Employee emp = employeeRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (!req.getEmployee().getId().equals(emp.getId())) {
            throw new SecurityException("Bạn chỉ có thể sửa đơn của chính mình");
        }

        if (dto.getType() != null) req.setType(dto.getType());
        if (dto.getDuration() != null) req.setDuration(dto.getDuration());
        if (dto.getLeaveDate() != null) req.setLeaveDate(dto.getLeaveDate());
        if (dto.getReason() != null) req.setReason(dto.getReason());

        if (req.getLeaveDate().isBefore(emp.getOnboardDate())) {
            throw new RuntimeException("Không thể chỉnh đơn nghỉ về trước ngày onboard");
        }

        boolean overlap = hasOverlap(
                emp.getId(),
                req.getId(),
                req.getLeaveDate()
        );
        if (overlap) {
            throw new IllegalStateException("Đơn nghỉ bị trùng ngày");
        }

        LeaveType finalType = dto.getType() != null ? dto.getType() : req.getType();
        
        if (finalType == LeaveType.ANNUAL) {
            double newAmount = getDurationValue(req.getDuration());

            LocalDate leaveDate = req.getLeaveDate();
            int year = leaveDate.getYear();

            double quota = getCurrentQuota(emp, leaveDate);
            double used = calculateUsedQuota(emp.getId(), req.getId(), year);

            if (used + newAmount > quota) {
            	throw new IllegalStateException(
            		    "Không đủ ngày phép. Còn lại: " + (quota - used)
            		);
            }
        }

        LeaveRequest saved = leaveRequestRepository.save(req);

        audit.log(userId, "LEAVE_UPDATE",
                "Cập nhật đơn nghỉ phép, mã đơn = " + id);

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
        
        if (req.getEmployee().getId().equals(actorId)) {
            throw new SecurityException("Bạn không thể tự duyệt đơn của chính mình");
        }

        Employee emp = req.getEmployee();

        if (req.getLeaveDate().isBefore(emp.getOnboardDate())) {
            throw new RuntimeException("Không thể duyệt đơn nghỉ trước ngày onboard");
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
                saved.getEmployee().getId(),
                action == DecisionAction.APPROVE
                        ? "Đơn nghỉ phép đã được duyệt"
                        : "Đơn nghỉ phép bị từ chối",
                action == DecisionAction.APPROVE
                        ? "Đơn nghỉ phép ngày " + saved.getLeaveDate() + " đã được duyệt"
                        : "Đơn nghỉ phép ngày " + saved.getLeaveDate() + " đã bị từ chối",
                action == DecisionAction.APPROVE
                        ? NotificationType.LEAVE_APPROVED
                        : NotificationType.LEAVE_REJECTED
        );

        if (saved.getStatus() == LeaveStatus.APPROVED) {
            attendanceCalculationService.recalculateDaily(
                    saved.getEmployee().getId(),
                    saved.getLeaveDate()
            );
        }

        audit.log(actorId, "LEAVE_DECIDE",
                action == DecisionAction.APPROVE
                        ? "Duyệt đơn nghỉ phép, mã đơn = " + id
                        : "Từ chối đơn nghỉ phép, mã đơn = " + id
        );

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
                action == DecisionAction.APPROVE
                        ? "Duyệt hàng loạt đơn nghỉ phép: " + ids
                        : "Từ chối hàng loạt đơn nghỉ phép: " + ids
        );

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
        dto.setDuration(req.getDuration());
        dto.setLeaveDate(req.getLeaveDate());
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
