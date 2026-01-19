package com.tlu.hrm.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.tlu.hrm.entities.AttendanceRecord;
import com.tlu.hrm.entities.Department;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.entities.TimekeepingExplanation;
import com.tlu.hrm.enums.DecisionAction;
import com.tlu.hrm.enums.NotificationType;
import com.tlu.hrm.enums.TimekeepingExplanationStatus;
import com.tlu.hrm.repository.AttendanceRecordRepository;
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
    private final NotificationService notificationService;
    private final AttendanceRecordRepository attendanceRecordRepository;
    
	public TimekeepingExplanationServiceImpl(TimekeepingExplanationRepository repository,
			EmployeeRepository employeeRepository, ApprovalResolverService approvalResolverService,
			AttendanceCalculationService attendanceCalculationService, NotificationService notificationService,
			AttendanceRecordRepository attendanceRecordRepository) {
		super();
		this.repository = repository;
		this.employeeRepository = employeeRepository;
		this.approvalResolverService = approvalResolverService;
		this.attendanceCalculationService = attendanceCalculationService;
		this.notificationService = notificationService;
		this.attendanceRecordRepository =attendanceRecordRepository;
	}

	// =====================================================
    // Helpers
    // =====================================================
    private Employee getCurrentEmployee() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails ud = (CustomUserDetails) auth.getPrincipal();

        return employeeRepository.findByUserId(ud.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
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
        
        if (emp.getDepartment() == null) {
            throw new RuntimeException("Nhân viên chưa được gán phòng ban");
        }

        Long approverId = approvalResolverService.resolveApproverId(
                emp.getId(),
                emp.getDepartment().getId()
        );
        
        if (approverId.equals(emp.getId())) {
            throw new RuntimeException(
                "Bạn không thể tự duyệt đơn của chính mình. Vui lòng liên hệ HR/Admin để được gán người duyệt."
            );
        }
        
        if (dto.getWorkDate() == null) {
            throw new IllegalArgumentException("Ngày làm việc không được để trống");
        }
        
        if (dto.getProposedCheckIn() != null && dto.getProposedCheckOut() != null
                && !dto.getProposedCheckIn().isBefore(dto.getProposedCheckOut())) {
            throw new IllegalArgumentException("Giờ vào phải trước giờ ra");
        }
        
        if (dto.getWorkDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Không thể giải trình cho ngày tương lai");
        }
        
        if (emp.getOnboardDate() != null && dto.getWorkDate().isBefore(emp.getOnboardDate())) {
            throw new IllegalArgumentException("Không thể giải trình cho ngày trước khi onboard");
        }
        
        if (repository.existsByEmployeeIdAndWorkDate(emp.getId(), dto.getWorkDate())) {
            throw new RuntimeException("Bạn đã tạo giải trình cho ngày này rồi");
        }
        
        TimekeepingExplanation e = new TimekeepingExplanation();
        e.setEmployee(emp);
        e.setWorkDate(dto.getWorkDate());
        e.setProposedCheckIn(dto.getProposedCheckIn());
        e.setProposedCheckOut(dto.getProposedCheckOut());
        e.setReason(dto.getReason());
        e.setStatus(TimekeepingExplanationStatus.PENDING);
        e.setApproverId(approverId);
        
        AttendanceRecord ar = attendanceRecordRepository
        	    .findByEmployeeIdAndWorkDate(emp.getId(), dto.getWorkDate())
        	    .orElse(null);
        
    	if (ar != null) {
    	    e.setOriginalCheckIn(ar.getCheckIn());
    	    e.setOriginalCheckOut(ar.getCheckOut());
    	}

        
        TimekeepingExplanation saved = repository.save(e);

        notificationService.createNotification(
                saved.getApproverId(), // người duyệt
                "Có giải trình công mới",
                "Nhân viên " + emp.getFullName()
                        + " đã gửi giải trình công cho ngày "
                        + saved.getWorkDate(),
                NotificationType.EXPLANATION_REQUEST
        );

        return toDTO(saved);
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
     boolean isOnlyEmployee =
    	        roles.contains("ROLE_EMPLOYEE")
    	        && !roles.contains("ROLE_MANAGER")
    	        && !roles.contains("ROLE_HR");

	if (isOnlyEmployee) {
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
    
    @Override
    public Page<TimekeepingExplanationResponseDTO> getPending(int page, int size) {

        Employee actor = getCurrentEmployee();
        Set<String> roles = getRoles();

        if (!roles.contains("ROLE_HR") && !roles.contains("ROLE_MANAGER")) {
            throw new AccessDeniedException("Bạn không có quyền truy cập");
        }

        Long approverEmployeeId = actor.getId();

        Set<Long> approvedEmployeeIds =
                approvalResolverService.getApprovedEmployeeIds(approverEmployeeId);

        Set<Long> approvedDepartmentIds =
                approvalResolverService.getApprovedDepartmentIds(approverEmployeeId);

        Pageable pageable = PageRequest.of(
                page, size, Sort.by("createdAt").descending()
        );

        TimekeepingExplanationFilterDTO filter = new TimekeepingExplanationFilterDTO();
        filter.setStatus(TimekeepingExplanationStatus.PENDING);

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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giải trình"));

        Employee actor = getCurrentEmployee();
        Set<String> roles = getRoles();

        boolean canView =
                roles.contains("ROLE_HR")
                || e.getEmployee().getId().equals(actor.getId())
                || (
                    roles.contains("ROLE_MANAGER")
                    && e.getEmployee().getDepartment() != null
                    && actor.getDepartment() != null
                    && e.getEmployee().getDepartment().getId()
                           .equals(actor.getDepartment().getId())
                )
                || actor.getId().equals(e.getApproverId());

        if (!canView) {
            throw new AccessDeniedException("Bạn không có quyền truy cập");
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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giải trình"));

        if (e.getStatus() != TimekeepingExplanationStatus.PENDING) {
            throw new IllegalStateException("Đơn đã được xử lý");
        }
        
        if (e.getEmployee().getOnboardDate() != null && e.getWorkDate().isBefore(e.getEmployee().getOnboardDate())) {
            throw new RuntimeException("Không thể duyệt giải trình trước ngày onboard");
        }

        Employee actor = getCurrentEmployee();
        Long actorEmployeeId = actor.getId();

        if (!actorEmployeeId.equals(e.getApproverId())) {
            throw new AccessDeniedException("Bạn không phải người duyệt");
        }
        
        if (e.getEmployee().getId().equals(actor.getId())) {
            throw new AccessDeniedException("Bạn không thể tự duyệt giải trình của chính mình");
        }
        
        if (dto.getAction() == DecisionAction.APPROVE) {
            if (e.getProposedCheckIn() == null && e.getProposedCheckOut() == null) {
                throw new IllegalArgumentException("Không có giờ đề xuất để duyệt");
            }
        }
        
        if (dto.getAction() == DecisionAction.APPROVE) {
            if (e.getProposedCheckIn() != null && e.getProposedCheckOut() != null
                && !e.getProposedCheckIn().isBefore(e.getProposedCheckOut())) {
                throw new IllegalArgumentException("Giờ vào phải trước giờ ra");
            }
        }
        
        e.setStatus(
                dto.getAction() == DecisionAction.APPROVE
                        ? TimekeepingExplanationStatus.APPROVED
                        : TimekeepingExplanationStatus.REJECTED
        );

        e.setDecidedBy(actorEmployeeId);
        e.setDecidedAt(LocalDateTime.now());
        e.setManagerNote(dto.getManagerNote());

        if (dto.getAction() == DecisionAction.APPROVE) {

            AttendanceRecord ar = attendanceRecordRepository
                .findByEmployeeIdAndWorkDate(
                    e.getEmployee().getId(),
                    e.getWorkDate()
                )
                .orElse(null);

            if (ar != null) {
                if (e.getProposedCheckIn() != null) {
                    ar.setCheckIn(e.getProposedCheckIn());
                }
                if (e.getProposedCheckOut() != null) {
                    ar.setCheckOut(e.getProposedCheckOut());
                }

                attendanceRecordRepository.save(ar);
            }

            attendanceCalculationService.recalculateDaily(
                e.getEmployee().getId(),
                e.getWorkDate()
            );
        }

        TimekeepingExplanation saved = e; // entity đang managed

        notificationService.createNotification(
                saved.getEmployee().getId(), // nhân viên
                dto.getAction() == DecisionAction.APPROVE
                        ? "Giải trình công đã được duyệt"
                        : "Giải trình công bị từ chối",
                dto.getAction() == DecisionAction.APPROVE
                        ? "Giải trình công ngày " + saved.getWorkDate() + " đã được duyệt"
                        : "Giải trình công ngày " + saved.getWorkDate() + " đã bị từ chối",
                dto.getAction() == DecisionAction.APPROVE
                        ? NotificationType.EXPLANATION_APPROVED
                        : NotificationType.EXPLANATION_REJECTED
        );

        return toDTO(saved);
    }

    // =====================================================
    // DECIDE MANY
    // =====================================================
    @Override
    @Transactional
    public BulkDecisionResultDTO decideMany(BulkDecisionDTO dto) {

        List<Long> success = new ArrayList<>();
        List<Long> failed = new ArrayList<>();
        Map<Long, String> failedReasons = new HashMap<>();

        for (Long id : dto.getIds()) {
            try {
                decide(id,
                    new TimekeepingExplanationDecisionDTO() {{
                        setAction(dto.getAction());
                        setManagerNote(dto.getManagerNote());
                    }}
                );
                success.add(id);
            } catch (AccessDeniedException e) {
                failed.add(id);
                failedReasons.put(id, e.getMessage());
            } catch (IllegalStateException e) {
                failed.add(id);
                failedReasons.put(id, translateIllegalState(e.getMessage()));
            } catch (RuntimeException e) {
                failed.add(id);
                failedReasons.put(id, translateRuntime(e.getMessage()));
            } catch (Exception e) {
                failed.add(id);
                failedReasons.put(id, "Lỗi không xác định");
            }
        }

        return new BulkDecisionResultDTO(success, failed, failedReasons);
    }
    
    private String translateIllegalState(String msg) {
        if (msg.contains("Đơn đã được xử lý")) {
            return "Đơn này đã được xử lý trước đó";
        }
        return msg;
    }
    
    private String translateRuntime(String msg) {
        if (msg.contains("Không tìm thấy")) {
            return "Không tìm thấy giải trình";
        }
        if (msg.contains("onboard")) {
            return "Không thể duyệt giải trình trước ngày onboard";
        }
        return msg;
    }

    // =====================================================
    // Mapper
    // =====================================================
    private TimekeepingExplanationResponseDTO toDTO(TimekeepingExplanation e) {

        TimekeepingExplanationResponseDTO dto =
                new TimekeepingExplanationResponseDTO();

        Employee emp = e.getEmployee();
        Department dept = emp.getDepartment();
        if (dept != null) {
            dto.setDepartmentId(dept.getId());
            dto.setDepartmentName(dept.getName());
        }

        dto.setId(e.getId());
        dto.setEmployeeId(emp.getId());
        dto.setEmployeeCode(emp.getCode());
        dto.setEmployeeName(emp.getFullName());

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
