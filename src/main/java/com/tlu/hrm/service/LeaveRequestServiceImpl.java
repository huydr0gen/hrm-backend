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
    
    private double getDurationValue(LeaveDuration duration) {
        if (duration == null) return 1.0;
        return switch (duration) {
            case FULL_DAY -> 1.0;
            case MORNING, AFTERNOON -> 0.5;
        };
    }
    
    private double calculateLeaveAmount(LocalDate start, LocalDate end, LeaveDuration duration) {
        long days = ChronoUnit.DAYS.between(start, end) + 1;
        return days * getDurationValue(duration);
    }
    
    private boolean isOverlap(LeaveRequest a, LocalDate newStart, LocalDate newEnd, LeaveDuration newDuration) {

        LocalDate start = a.getStartDate().isAfter(newStart) ? a.getStartDate() : newStart;
        LocalDate end = a.getEndDate().isBefore(newEnd) ? a.getEndDate() : newEnd;

        if (start.isAfter(end)) return false; // không giao ngày

        LeaveDuration oldDur = a.getDuration() != null ? a.getDuration() : LeaveDuration.FULL_DAY;
        LeaveDuration newDur = newDuration != null ? newDuration : LeaveDuration.FULL_DAY;

        if (oldDur == LeaveDuration.FULL_DAY || newDur == LeaveDuration.FULL_DAY) {
            return true;
        }

        return oldDur == newDur;
    }
    
    private boolean hasOverlap(Long employeeId, Long excludeId, LocalDate start, LocalDate end, LeaveDuration duration) {

        List<LeaveRequest> existing = leaveRequestRepository.findByEmployeeIdAndStatusIn(
                employeeId,
                List.of(LeaveStatus.APPROVED, LeaveStatus.PENDING)
        );

        for (LeaveRequest lr : existing) {
            if (excludeId != null && lr.getId().equals(excludeId)) continue;

            if (isOverlap(lr, start, end, duration)) {
                return true;
            }
        }

        return false;
    }
    
    private double calculateUsedQuota(Long employeeId, Long excludeId) {
        List<LeaveRequest> list;

        if (excludeId == null) {
            list = leaveRequestRepository.findForQuota(
                    employeeId,
                    LeaveType.ANNUAL,
                    List.of(LeaveStatus.APPROVED, LeaveStatus.PENDING)
            );
        } else {
            list = leaveRequestRepository.findForQuotaExclude(
                    employeeId,
                    excludeId,
                    LeaveType.ANNUAL,
                    List.of(LeaveStatus.APPROVED, LeaveStatus.PENDING)
            );
        }

        return list.stream()
                .mapToDouble(lr ->
                        calculateLeaveAmount(
                                lr.getStartDate(),
                                lr.getEndDate(),
                                lr.getDuration()
                        )
                )
                .sum();
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

        if (dto.getStartDate().isBefore(emp.getOnboardDate())) {
            throw new RuntimeException("Không thể xin nghỉ trước ngày onboard");
        }

        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new IllegalArgumentException("Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc");
        }

        boolean overlap = hasOverlap(
                emp.getId(),
                null,
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getDuration()
        );
        if (overlap) {
            throw new IllegalStateException("Đơn nghỉ bị trùng thời gian");
        }

        if (dto.getType() == LeaveType.ANNUAL) {

            double used = calculateUsedQuota(emp.getId(), null);
            double newAmount = calculateLeaveAmount(
                    dto.getStartDate(),
                    dto.getEndDate(),
                    dto.getDuration()
            );

            if (used + newAmount > ANNUAL_LEAVE_QUOTA) {
                throw new IllegalStateException("Vượt quá số ngày phép năm cho phép");
            }
        }

        Long approverId = approvalResolverService.resolveApproverId(
                emp.getId(),
                emp.getDepartment().getId()
        );

        LeaveRequest req = new LeaveRequest();
        req.setEmployee(emp);
        req.setType(dto.getType());
        req.setDuration(dto.getDuration());
        req.setStartDate(dto.getStartDate());
        req.setEndDate(dto.getEndDate());
        req.setReason(dto.getReason());
        req.setStatus(LeaveStatus.PENDING);
        req.setApproverId(approverId);

        LeaveRequest saved = leaveRequestRepository.save(req);

        notificationService.createNotification(
                saved.getApproverId(),
                "Có đơn nghỉ phép mới",
                "Nhân viên " + emp.getFullName()
                        + " đã gửi đơn nghỉ phép từ "
                        + saved.getStartDate()
                        + " đến "
                        + saved.getEndDate(),
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
        if (dto.getStartDate() != null) req.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) req.setEndDate(dto.getEndDate());
        if (dto.getReason() != null) req.setReason(dto.getReason());

        if (req.getStartDate().isAfter(req.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        if (req.getStartDate().isBefore(emp.getOnboardDate())) {
            throw new RuntimeException("Không thể chỉnh đơn nghỉ về trước ngày onboard");
        }

        boolean overlap = hasOverlap(
                emp.getId(),
                req.getId(),
                req.getStartDate(),
                req.getEndDate(),
                req.getDuration()
        );
        if (overlap) {
            throw new IllegalStateException("Đơn nghỉ bị trùng thời gian");
        }

        if (req.getType() == LeaveType.ANNUAL) {

            double used = calculateUsedQuota(emp.getId(), req.getId());
            double newAmount = calculateLeaveAmount(
                    req.getStartDate(),
                    req.getEndDate(),
                    req.getDuration()
            );

            if (used + newAmount > ANNUAL_LEAVE_QUOTA) {
                throw new IllegalStateException("Vượt quá số ngày phép năm cho phép");
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
        
        Employee emp = req.getEmployee();

        if (req.getStartDate().isBefore(emp.getOnboardDate())) {
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
