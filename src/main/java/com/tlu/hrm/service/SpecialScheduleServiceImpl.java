package com.tlu.hrm.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tlu.hrm.dto.SpecialScheduleApproveDTO;
import com.tlu.hrm.dto.SpecialScheduleBulkApproveDTO;
import com.tlu.hrm.dto.SpecialScheduleCreateDTO;
import com.tlu.hrm.dto.SpecialScheduleFilterDTO;
import com.tlu.hrm.dto.SpecialScheduleResponseDTO;
import com.tlu.hrm.dto.SpecialScheduleUpdateDTO;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.entities.SpecialSchedule;
import com.tlu.hrm.enums.SpecialScheduleStatus;
import com.tlu.hrm.mapper.SpecialScheduleMapper;
import com.tlu.hrm.repository.EmployeeRepository;
import com.tlu.hrm.repository.SpecialScheduleRepository;
import com.tlu.hrm.security.CustomUserDetails;
import com.tlu.hrm.spec.SpecialScheduleSpecification;

@Service
public class SpecialScheduleServiceImpl implements SpecialScheduleService {

	private final SpecialScheduleRepository repository;
    private final EmployeeRepository employeeRepository;
    private final SpecialScheduleMapper mapper = new SpecialScheduleMapper();

    private static final Logger log = LoggerFactory.getLogger(SpecialScheduleServiceImpl.class);
    
	public SpecialScheduleServiceImpl(SpecialScheduleRepository repository, EmployeeRepository employeeRepository) {
		super();
		this.repository = repository;
		this.employeeRepository = employeeRepository;
	}

	// ================================
    //  HELPER: Kiểm tra xem Manager và nhân viên có cùng phòng ban không
    // ================================
    private boolean isInManagerDepartment(Long managerEmployeeId, Long targetEmployeeId) {

        Employee manager = employeeRepository.findById(managerEmployeeId)
                .orElseThrow(() -> new RuntimeException("Manager employee not found"));

        Employee target = employeeRepository.findById(targetEmployeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (manager.getDepartment() == null || target.getDepartment() == null)
            return false;

        return manager.getDepartment().equalsIgnoreCase(target.getDepartment());
    }

    // ================================
    // LIST (phân quyền theo role + phòng ban)
    // ================================
    @Override
    public Page<SpecialScheduleResponseDTO> list(SpecialScheduleFilterDTO filter, Pageable pageable) {

        log.info("Listing SpecialSchedule with filter {}", filter);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails currentUser = (CustomUserDetails) auth.getPrincipal();

        Long currentEmployeeId = currentUser.getEmployeeId();
        String roles = auth.getAuthorities().toString();

        // EMPLOYEE -> chỉ xem lịch của chính họ
        if (roles.contains("ROLE_EMPLOYEE")) {
            filter.setEmployeeId(currentEmployeeId);
            log.info("EMPLOYEE -> filter only their schedules, employeeId={}", currentEmployeeId);
        }

        // MANAGER -> xem danh sách nhân viên trong phòng ban
        else if (roles.contains("ROLE_MANAGER")) {

            Employee manager = employeeRepository.findById(currentEmployeeId)
                    .orElseThrow(() -> new RuntimeException("Manager employee not found"));

            List<Long> employeeIds = employeeRepository.findByDepartment(manager.getDepartment())
                    .stream().map(Employee::getId).toList();

            filter.setEmployeeIds(employeeIds);

            log.info("MANAGER -> filter by department={}, employeeIds={}",
                    manager.getDepartment(), employeeIds);
        }

        // HR + ADMIN -> xem tất cả

        return repository.findAll(SpecialScheduleSpecification.build(filter), pageable)
                .map(mapper::toResponse);
    }

    // ================================
    // CREATE
    // ================================
    @Override
    public SpecialScheduleResponseDTO create(SpecialScheduleCreateDTO dto) {
        log.info("Creating SpecialSchedule for employee {}", dto.getEmployeeId());

        SpecialSchedule entity = mapper.toEntity(dto, "system");

        repository.save(entity);
        return mapper.toResponse(entity);
    }

    // ================================
    // UPDATE (EMPLOYEE không được phép)
    // MANAGER/HR/ADMIN -> được phép
    // ================================
    @Override
    public SpecialScheduleResponseDTO update(Long id, SpecialScheduleUpdateDTO dto) {

        log.info("Updating SpecialSchedule {}", id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String roles = auth.getAuthorities().toString();
        CustomUserDetails currentUser = (CustomUserDetails) auth.getPrincipal();

        // EMPLOYEE không được update
        if (roles.contains("ROLE_EMPLOYEE")) {
            throw new AccessDeniedException("Employees cannot update schedules");
        }

        // Tải dữ liệu lịch
        SpecialSchedule schedule = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SpecialSchedule not found"));

        // MANAGER -> chỉ update nhân viên trong phòng ban của họ
        if (roles.contains("ROLE_MANAGER")) {
            if (!isInManagerDepartment(currentUser.getEmployeeId(), schedule.getEmployeeId())) {
                throw new AccessDeniedException("Manager cannot update schedules outside their department");
            }
        }

        // HR + ADMIN -> OK

        if (schedule.getStatus() != SpecialScheduleStatus.PENDING) {
            throw new RuntimeException("Only PENDING schedules can be updated");
        }

        mapper.updateEntity(schedule, dto);
        repository.save(schedule);

        return mapper.toResponse(schedule);
    }

    // ================================
    // APPROVE (duyệt 1 lịch)
    // EMPLOYEE -> không được
    // MANAGER -> chỉ duyệt nhân viên trong phòng ban
    // HR/ADMIN -> full
    // ================================
    @Override
    public SpecialScheduleResponseDTO approve(Long id, SpecialScheduleApproveDTO dto) {

        log.info("Approving schedule {} with status {}", id, dto.getStatus());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String roles = auth.getAuthorities().toString();
        CustomUserDetails currentUser = (CustomUserDetails) auth.getPrincipal();

        // EMPLOYEE không được duyệt
        if (roles.contains("ROLE_EMPLOYEE")) {
            throw new AccessDeniedException("Employees cannot approve schedules");
        }

        SpecialSchedule schedule = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SpecialSchedule not found"));

        // MANAGER -> chỉ duyệt nhân viên cùng phòng ban
        if (roles.contains("ROLE_MANAGER")) {
            if (!isInManagerDepartment(currentUser.getEmployeeId(), schedule.getEmployeeId())) {
                throw new AccessDeniedException("Manager cannot approve schedules outside their department");
            }
        }

        // HR + ADMIN → full quyền

        if (schedule.getStatus() != SpecialScheduleStatus.PENDING) {
            throw new RuntimeException("Schedule already processed");
        }

        schedule.setStatus(dto.getStatus());
        schedule.setApprovedBy("system");
        schedule.setApprovedAt(LocalDateTime.now());

        repository.save(schedule);
        return mapper.toResponse(schedule);
    }

    // ================================
    // APPROVE MANY (duyệt nhiều lịch)
    // ADMIN -> full
    // MANAGER -> chỉ duyệt nhiều nếu toàn bộ thuộc phòng ban của họ
    // HR -> không bulk approve (tùy chính sách, nhưng bạn yêu cầu như vậy)
    // EMPLOYEE -> không được
    // ================================
    @Override
    public int approveMany(SpecialScheduleBulkApproveDTO dto) {

        log.info("Bulk approving schedules");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String roles = auth.getAuthorities().toString();
        CustomUserDetails currentUser = (CustomUserDetails) auth.getPrincipal();

        // EMPLOYEE -> không được approve-many
        if (roles.contains("ROLE_EMPLOYEE")) {
            throw new AccessDeniedException("Employees cannot approve schedules");
        }

        // HR -> không được bulk approve
        if (roles.contains("ROLE_HR")) {
            throw new AccessDeniedException("HR cannot bulk approve schedules");
        }

        List<SpecialSchedule> schedules = repository.findAllById(dto.getIds());

        // MANAGER -> tất cả phải thuộc phòng ban họ quản lý
        if (roles.contains("ROLE_MANAGER")) {
            for (SpecialSchedule s : schedules) {
                if (!isInManagerDepartment(currentUser.getEmployeeId(), s.getEmployeeId())) {
                    throw new AccessDeniedException(
                            "Manager cannot bulk approve schedules outside their department");
                }
            }
        }

        // ADMIN -> FULL QUYỀN

        int count = 0;
        for (SpecialSchedule e : schedules) {
            if (e.getStatus() == SpecialScheduleStatus.PENDING) {
                e.setStatus(dto.getStatus());
                e.setApprovedBy("system");
                e.setApprovedAt(LocalDateTime.now());
                count++;
            }
        }

        repository.saveAll(schedules);
        return count;
    }

    // ================================
    // DETAIL (phân quyền đầy đủ)
    // ================================
    @Override
    public SpecialScheduleResponseDTO detail(Long id) {

        log.info("Getting detail schedule {}", id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails currentUser = (CustomUserDetails) auth.getPrincipal();
        String roles = auth.getAuthorities().toString();

        SpecialSchedule schedule = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SpecialSchedule not found"));

        // EMPLOYEE -> chỉ xem lịch của chính họ
        if (roles.contains("ROLE_EMPLOYEE")) {
            if (!schedule.getEmployeeId().equals(currentUser.getEmployeeId())) {
                throw new AccessDeniedException("You cannot view schedules of other employees");
            }
        }

        // MANAGER -> chỉ xem lịch của nhân viên phòng ban họ
        if (roles.contains("ROLE_MANAGER")) {
            if (!isInManagerDepartment(currentUser.getEmployeeId(), schedule.getEmployeeId())) {
                throw new AccessDeniedException("Manager cannot view schedules outside their department");
            }
        }

        // HR + ADMIN -> full quyền

        return mapper.toResponse(schedule);
    }
}
