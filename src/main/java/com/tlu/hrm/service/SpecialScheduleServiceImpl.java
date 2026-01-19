package com.tlu.hrm.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.tlu.hrm.enums.Gender;
import com.tlu.hrm.enums.NotificationType;
import com.tlu.hrm.enums.SpecialScheduleStatus;
import com.tlu.hrm.repository.EmployeeRepository;
import com.tlu.hrm.repository.SpecialScheduleRepository;
import com.tlu.hrm.security.CustomUserDetails;
import com.tlu.hrm.spec.SpecialScheduleSpecification;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SpecialScheduleServiceImpl implements SpecialScheduleService {
	
	private static final LocalTime MORNING_END = LocalTime.of(12, 0);
	private static final LocalTime AFTERNOON_START = LocalTime.of(13, 0);

	private final SpecialScheduleRepository repository;
    private final EmployeeRepository employeeRepository;
    private final ApprovalResolverService approvalResolverService;
    private final AttendanceCalculationService attendanceCalculationService;
    private final NotificationService notificationService;

	public SpecialScheduleServiceImpl(SpecialScheduleRepository repository, EmployeeRepository employeeRepository,
			ApprovalResolverService approvalResolverService,
			AttendanceCalculationService attendanceCalculationService, NotificationService notificationService) {
		super();
		this.repository = repository;
		this.employeeRepository = employeeRepository;
		this.approvalResolverService = approvalResolverService;
		this.attendanceCalculationService = attendanceCalculationService;
		this.notificationService = notificationService;
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
        
        if (approverId.equals(emp.getId())) {
            throw new RuntimeException(
                "Bạn không thể tự duyệt đơn của chính mình. Vui lòng liên hệ HR/Admin để được gán người duyệt."
            );
        }       

        SpecialSchedule ss = new SpecialSchedule();
        ss.setEmployee(emp);
        ss.setStartDate(dto.getStartDate());
        ss.setType(dto.getType());
        ss.setReason(dto.getReason());
        ss.setStatus(SpecialScheduleStatus.PENDING);
        ss.setApproverId(approverId);

        switch (dto.getType()) {

            case MATERNITY -> {
            	if (emp.getGender() == null || emp.getGender() != Gender.FEMALE) {
                    throw new IllegalArgumentException(
                        "Không đủ điều kiện đăng ký nghỉ thai sản (chỉ áp dụng cho nhân viên nữ)"
                    );
                }

                ss.setEndDate(dto.getStartDate().plusMonths(6));
            }

            case CHILD_CARE -> {
            	Gender gender = emp.getGender();

                if (gender == null) {
                    throw new IllegalArgumentException(
                        "Vui lòng cập nhật giới tính trước khi đăng ký lịch chăm con nhỏ"
                    );
                }

                if (gender == Gender.FEMALE) {
                	// nữ - 7 tháng
                    ss.setEndDate(dto.getStartDate().plusMonths(7));
                } else if (gender == Gender.MALE) {
                    // nam -  14 ngày
                    ss.setEndDate(dto.getStartDate().plusDays(14));
                } else {
                    throw new IllegalArgumentException(
                        "Giới tính hiện tại không áp dụng cho chế độ nghỉ chăm con"
                    );
                }

                applyWorkingTime(ss, dto);
                validateChildCareWorkingTime(ss);
                ss.setWorkingHours(7); // 7h làm – 8h công
            }

            case ON_SITE -> {
                if (dto.getEndDate() == null) {
                    throw new IllegalArgumentException("endDate is required for ON_SITE");
                }
                validateOnSiteInfo(dto);
                ss.setEndDate(dto.getEndDate());
                mapOnSiteInfo(ss, dto);
                // ❌ không dùng giờ làm
            }

            case OTHER -> {
                if (dto.getEndDate() == null) {
                    throw new IllegalArgumentException("endDate is required for OTHER");
                }
                ss.setEndDate(dto.getEndDate());
                applyWorkingTime(ss, dto); // optional
            }
        }
        
        if (emp.getOnboardDate() != null
                && ss.getEndDate().isBefore(emp.getOnboardDate())) {
            throw new RuntimeException(
                "Không thể tạo lịch đặc thù nằm hoàn toàn trước ngày onboard"
            );
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
            throw new IllegalStateException("Đã tồn tại lịch đặc thù cùng loại trong khoảng thời gian này");
        }
        
        SpecialSchedule saved = repository.save(ss);

        notificationService.createNotification(
                saved.getApproverId(),
                "Có lịch đặc thù mới",
                "Nhân viên " + emp.getFullName()
                        + " đã tạo lịch đặc thù từ "
                        + saved.getStartDate()
                        + " đến "
                        + saved.getEndDate(),
                NotificationType.SPECIAL_SCHEDULE_REQUEST
        );

        return toDTO(saved);
    }
    
    @Override
    public Page<SpecialScheduleResponseDTO> getMySchedules(int page, int size) {

        requireRole("ROLE_EMPLOYEE");

        Employee emp = getCurrentEmployee();
        Pageable pageable = PageRequest.of(page, size);

        return repository.findAll(
                (root, q, cb) ->
                        cb.equal(root.get("employee").get("id"), emp.getId()),
                pageable
        ).map(this::toDTO);
    }
    
    @Override
    public Page<SpecialScheduleResponseDTO> getDepartmentSchedules(int page, int size) {

        requireRole("ROLE_MANAGER");

        Employee manager = getCurrentEmployee();
        Long deptId = manager.getDepartment().getId();
        Pageable pageable = PageRequest.of(page, size);

        return repository.findAll(
                (root, q, cb) ->
                        cb.equal(
                            root.get("employee")
                                .get("department")
                                .get("id"),
                            deptId
                        ),
                pageable
        ).map(this::toDTO);
    }
    
    @Override
    public Page<SpecialScheduleResponseDTO> getPendingForApprover(int page, int size) {

        Employee actor = getCurrentEmployee();
        Long approverEmployeeId = actor.getId();

        var approvedEmployeeIds =
                approvalResolverService.getApprovedEmployeeIds(approverEmployeeId);

        var approvedDepartmentIds =
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
    
    @Override
    public Page<SpecialScheduleResponseDTO> list(SpecialScheduleFilterDTO filter) {

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

        return toDTO(ss);
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
            throw new AccessDeniedException("Can only update your own schedule");
        }

        if (ss.getStatus() != SpecialScheduleStatus.PENDING) {
            throw new IllegalStateException("Only PENDING schedule can be updated");
        }
        
        LocalDate newStart = dto.getStartDate() != null ? dto.getStartDate() : ss.getStartDate();
        LocalDate newEnd = dto.getEndDate() != null ? dto.getEndDate() : ss.getEndDate();

        if (actor.getOnboardDate() != null
                && newEnd.isBefore(actor.getOnboardDate())) {
            throw new RuntimeException(
                "Không thể chỉnh lịch đặc thù nằm hoàn toàn trước ngày onboard"
            );
        }
        
        switch (ss.getType()) {

            case MATERNITY -> {
                // không cho chỉnh gì
            }

            case CHILD_CARE -> {
                if (dto.getStartDate() != null) {
                    ss.setStartDate(dto.getStartDate());
                }
                if (dto.getEndDate() != null) {
                    ss.setEndDate(dto.getEndDate());
                }
                applyWorkingTime(ss, dto);
                validateChildCareWorkingTime(ss);
            }

            case ON_SITE -> {
                if (dto.getStartDate() != null) {
                    ss.setStartDate(dto.getStartDate());
                }
                if (dto.getEndDate() != null) {
                    ss.setEndDate(dto.getEndDate());
                }
                mapOnSiteInfo(ss, dto);
            }

            case OTHER -> {
                if (dto.getStartDate() != null) {
                    ss.setStartDate(dto.getStartDate());
                }
                if (dto.getEndDate() != null) {
                    ss.setEndDate(dto.getEndDate());
                }
                applyWorkingTime(ss, dto);
            }
        }

        ss.setReason(dto.getReason());

        boolean overlap = repository.existsOverlappingScheduleExcludeId(
                actor,
                ss.getType(),
                List.of(
                        SpecialScheduleStatus.PENDING,
                        SpecialScheduleStatus.APPROVED
                ),
                ss.getStartDate(),
                ss.getEndDate(),
                ss.getId()
        );

        if (overlap) {
            throw new IllegalStateException(
                "Đã tồn tại lịch đặc thù cùng loại trong khoảng thời gian này"
            );
        }

        return toDTO(repository.save(ss));
    }
    
	 // ======================================================
	 // DELETE (EMPLOYEE + OWN + PENDING)
	 // ======================================================
	 @Override
	 public void delete(Long id) {
	
	     requireRole("ROLE_EMPLOYEE");
	
	     Employee actor = getCurrentEmployee();
	     SpecialSchedule ss = getById(id);
	
	     // chỉ được xoá đơn của chính mình
	     if (!ss.getEmployee().getId().equals(actor.getId())) {
	         throw new AccessDeniedException(
	                 "Can only delete your own schedule"
	         );
	     }
	
	     // chỉ xoá khi còn PENDING
	     if (ss.getStatus() != SpecialScheduleStatus.PENDING) {
	         throw new IllegalStateException(
	                 "Only PENDING schedule can be deleted"
	         );
	     }
	
	     repository.delete(ss);
	 }

    // ======================================================
    // DECIDE
    // ======================================================
    @Override
    public SpecialScheduleResponseDTO decide(Long id, DecisionAction action) {

        SpecialSchedule ss = getById(id);

        Employee actor = getCurrentEmployee();
        
        Employee emp = ss.getEmployee();
        
        Long actorEmployeeId = actor.getId();

        if (!actorEmployeeId.equals(ss.getApproverId())) {
            throw new AccessDeniedException("Bạn không phải là người được phân quyền duyệt đơn này");
        }

        if (ss.getEmployee().getId().equals(actorEmployeeId)) {
            throw new AccessDeniedException("Bạn không thể tự duyệt đơn của chính mình");
        }
        
        if (emp.getOnboardDate() != null
                && ss.getEndDate().isBefore(emp.getOnboardDate())) {
            throw new RuntimeException(
                "Không thể duyệt lịch đặc thù nằm hoàn toàn trước ngày onboard"
            );
        }
        
        if (ss.getStatus() != SpecialScheduleStatus.PENDING) {
            throw new IllegalStateException("Already processed");
        }

        ss.setStatus(
                action == DecisionAction.APPROVE
                        ? SpecialScheduleStatus.APPROVED
                        : SpecialScheduleStatus.REJECTED
        );

        ss.setDecidedBy(actorEmployeeId);
        ss.setDecidedAt(LocalDateTime.now());
        
        SpecialSchedule saved = repository.save(ss);

        if (ss.getStatus() == SpecialScheduleStatus.APPROVED) {
            LocalDate d = ss.getStartDate();
            while (!d.isAfter(ss.getEndDate())) {
                attendanceCalculationService.recalculateDaily(
                        ss.getEmployee().getId(),
                        d
                );
                d = d.plusDays(1);
            }
        }

        notificationService.createNotification(
        		saved.getEmployee().getId(), // nhân viên
                action == DecisionAction.APPROVE
                        ? "Lịch đặc thù đã được duyệt"
                        : "Lịch đặc thù bị từ chối",
                action == DecisionAction.APPROVE
                        ? "Lịch đặc thù từ " + saved.getStartDate()
                                + " đến " + saved.getEndDate() + " đã được duyệt"
                        : "Lịch đặc thù từ " + saved.getStartDate()
                                + " đến " + saved.getEndDate() + " đã bị từ chối",
                action == DecisionAction.APPROVE
                        ? NotificationType.SPECIAL_SCHEDULE_APPROVED
                        : NotificationType.SPECIAL_SCHEDULE_REJECTED
        );
        
        return toDTO(saved);
    }
    
    @Override
    public BulkDecisionResultDTO decideMany(
            List<Long> ids,
            DecisionAction action) {

    	List<Long> success = new ArrayList<>();
        List<Long> failed = new ArrayList<>();
        Map<Long, String> failedReasons = new HashMap<>();

        for (Long id : ids) {
            try {
                decide(id, action);
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

    // ======================================================
    // WORKING TIME & VALIDATION
    // ======================================================
    private void applyWorkingTime(SpecialSchedule ss, SpecialScheduleCreateDTO dto) {
        ss.setMorningStart(dto.getMorningStart());
        ss.setAfternoonEnd(dto.getAfternoonEnd());
    }

    private void applyWorkingTime(SpecialSchedule ss, SpecialScheduleUpdateDTO dto) {
        ss.setMorningStart(dto.getMorningStart());
        ss.setAfternoonEnd(dto.getAfternoonEnd());
    }

    private void validateChildCareWorkingTime(SpecialSchedule ss) {
        long minutes = 0;
        
        if (ss.getMorningStart() != null && ss.getMorningStart().isAfter(MORNING_END)) {
            throw new IllegalArgumentException("Giờ bắt đầu buổi sáng phải trước 12:00");
        }

        if (ss.getAfternoonEnd() != null && ss.getAfternoonEnd().isBefore(AFTERNOON_START)) {
            throw new IllegalArgumentException("Giờ kết thúc buổi chiều phải sau 13:00");
        }

        if (ss.getMorningStart() != null) {
            minutes += Duration.between(
                    ss.getMorningStart(),
                    MORNING_END
            ).toMinutes();
        }

        if (ss.getAfternoonEnd() != null) {
            minutes += Duration.between(
                    AFTERNOON_START,
                    ss.getAfternoonEnd()
            ).toMinutes();
        }

        if (minutes < 7 * 60) {
            throw new IllegalArgumentException(
                    "Child care schedule must have at least 7 working hours"
            );
        }
    }

    private void validateOnSiteInfo(SpecialScheduleCreateDTO dto) {
        if (dto.getProjectCode() == null
                || dto.getProjectName() == null
                || dto.getManagerCode() == null
                || dto.getManagerName() == null) {

            throw new IllegalArgumentException(
                    "ON_SITE schedule requires project and manager information"
            );
        }
    }

    private void mapOnSiteInfo(SpecialSchedule ss, SpecialScheduleCreateDTO dto) {
        ss.setProjectCode(dto.getProjectCode());
        ss.setProjectName(dto.getProjectName());
        ss.setOnsiteManagerCode(dto.getManagerCode());
        ss.setOnsiteManagerName(dto.getManagerName());
    }

    private void mapOnSiteInfo(SpecialSchedule ss, SpecialScheduleUpdateDTO dto) {
        ss.setProjectCode(dto.getProjectCode());
        ss.setProjectName(dto.getProjectName());
        ss.setOnsiteManagerCode(dto.getManagerCode());
        ss.setOnsiteManagerName(dto.getManagerName());
    }

    // ======================================================
    // HELPERS
    // ======================================================
    private String translateIllegalState(String msg) {
        if ("Already processed".equals(msg)) {
            return "Đơn này đã được xử lý trước đó";
        }
        return "Trạng thái đơn không hợp lệ";
    }
    
    private String translateRuntime(String msg) {
        if (msg.contains("not found")) {
            return "Không tìm thấy đơn";
        }
        if (msg.contains("onboard")) {
            return "Lịch này trước ngày onboard, không thể duyệt";
        }
        return msg;
    }
    
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
        dto.setAfternoonEnd(e.getAfternoonEnd());

        dto.setType(e.getType());
        dto.setReason(e.getReason());
        
        dto.setProjectCode(e.getProjectCode());
        dto.setProjectName(e.getProjectName());
        dto.setManagerCode(e.getOnsiteManagerCode());
        dto.setManagerName(e.getOnsiteManagerName());

        dto.setStatus(e.getStatus());
        dto.setApproverId(e.getApproverId());
        dto.setDecidedBy(e.getDecidedBy());
        dto.setDecidedAt(e.getDecidedAt());
        dto.setCreatedAt(e.getCreatedAt());

        return dto;
    }
}
