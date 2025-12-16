package com.tlu.hrm.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    
	public LeaveRequestServiceImpl(LeaveRequestRepository leaveRequestRepository, EmployeeRepository employeeRepo,
			UserRepository userRepo, AuditLogService audit) {
		super();
		this.leaveRequestRepository = leaveRequestRepository;
		this.employeeRepo = employeeRepo;
		this.userRepo = userRepo;
		this.audit = audit;
	}
    
	// ----------------------------
    // Helper: resolve current user id
    // ----------------------------
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
            var user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            var emp = employeeRepo.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            return emp.getUser().getId();
        }

        throw new RuntimeException("Cannot resolve current user id");
    }

    // ----------------------------
    // CREATE REQUEST (Employee)
    // ----------------------------
    @Override
    public LeaveRequestDTO createRequest(LeaveRequestCreateDTO dto) {
        Long employeeId = dto.getEmployeeId();
        if (employeeId == null) {
            employeeId = getCurrentUserId();
        }

        Employee emp = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LeaveRequest req = new LeaveRequest();
        req.setEmployee(emp);
        req.setType(dto.getType());
        req.setStartDate(dto.getStartDate());
        req.setEndDate(dto.getEndDate());
        req.setReason(dto.getReason());
        req.setStatus(LeaveStatus.PENDING);
        req.setCreatedAt(LocalDateTime.now());

        LeaveRequest saved = leaveRequestRepository.save(req);

        Long auditUserId = emp.getUser() != null ? emp.getUser().getId() : null;
        audit.log(auditUserId, "LEAVE_CREATE", "Created leave request id=" + saved.getId());

        return toDTO(saved);
    }

    // ----------------------------
    // ADMIN UPDATE (HR / ADMIN)
    // ----------------------------
    @Override
    @Transactional
    public LeaveRequestDTO adminUpdate(Long id, LeaveRequestUpdateDTO dto, Long actorId) {
        LeaveRequest req = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        boolean changed = false;

        if (dto.getType() != null && dto.getType() != req.getType()) {
            req.setType(dto.getType());
            changed = true;
        }
        if (dto.getStartDate() != null && !dto.getStartDate().equals(req.getStartDate())) {
            req.setStartDate(dto.getStartDate());
            changed = true;
        }
        if (dto.getEndDate() != null && !dto.getEndDate().equals(req.getEndDate())) {
            req.setEndDate(dto.getEndDate());
            changed = true;
        }
        if (dto.getReason() != null && !dto.getReason().equals(req.getReason())) {
            req.setReason(dto.getReason());
            changed = true;
        }
        if (dto.getManagerNote() != null && !dto.getManagerNote().equals(req.getManagerNote())) {
            req.setManagerNote(dto.getManagerNote());
            changed = true;
        }

        if (!changed) {
            // nothing changed
            return toDTO(req);
        }

        req.setUpdatedAt(LocalDateTime.now());
        LeaveRequest saved = leaveRequestRepository.save(req);

        audit.log(actorId, "LEAVE_ADMIN_UPDATE", "Admin updated leave request id=" + id);

        return toDTO(saved);
    }

    // ----------------------------
    // DELETE (HR / ADMIN)
    // ----------------------------
    @Override
    public void delete(Long id) {
        LeaveRequest req = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        leaveRequestRepository.delete(req);
        audit.log(null, "LEAVE_DELETE_ADMIN", "Admin deleted leave request id=" + id);
    }

    @Override
    public void deleteMany(List<Long> ids) {
        List<LeaveRequest> list = leaveRequestRepository.findAllById(ids);
        if (list.isEmpty()) throw new RuntimeException("No requests found");
        leaveRequestRepository.deleteAll(list);
        audit.log(null, "LEAVE_BATCH_DELETE", "Admin deleted leave requests: " + ids.toString());
    }

    // ----------------------------
    // GET BY ID
    // ----------------------------
    @Override
    public LeaveRequestDTO getById(Long id) {
        LeaveRequest req = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        return toDTO(req);
    }

    // ----------------------------
    // EMPLOYEE - MY REQUESTS
    // ----------------------------
    @Override
    public Page<LeaveRequestDTO> getMyRequests(Long userId, int page, int size) {
        Employee emp = employeeRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<LeaveRequest> data = leaveRequestRepository.findAll(
                (root, q, cb) -> cb.equal(root.get("employee"), emp),
                pageable
        );

        return data.map(this::toDTO);
    }

    // ----------------------------
    // MANAGER - DEPARTMENT REQUESTS
    // ----------------------------
    @Override
    public Page<LeaveRequestDTO> getDepartmentRequests(Long managerUserId, int page, int size) {
        Employee manager = employeeRepo.findByUserId(managerUserId)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        String dept = manager.getDepartment();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<LeaveRequest> spec = LeaveRequestSpecification.hasDepartment(dept);

        return leaveRequestRepository.findAll(spec, pageable).map(this::toDTO);
    }

    // ----------------------------
    // HR + ADMIN FILTER
    // ----------------------------
    @Override
    public Page<LeaveRequestDTO> getAllFiltered(String employeeName, String department, String status,
                                                String type, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<LeaveRequest> spec = Specification
                .where(LeaveRequestSpecification.hasEmployeeName(employeeName))
                .and(LeaveRequestSpecification.hasDepartment(department))
                .and(LeaveRequestSpecification.hasStatus(status != null ? LeaveStatus.valueOf(status) : null))
                .and(LeaveRequestSpecification.hasType(type != null ? LeaveType.valueOf(type) : null));

        return leaveRequestRepository.findAll(spec, pageable).map(this::toDTO);
    }

    // ----------------------------
    // DECIDE (single)
    // ----------------------------
    @Override
    @Transactional
    public LeaveRequestDTO decide(Long id, DecisionAction action, String comment, Long actorId) {

        LeaveRequest lr = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        if (lr.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Already processed");
        }

        if (action == DecisionAction.APPROVE) {
            lr.setStatus(LeaveStatus.APPROVED);
        } else if (action == DecisionAction.REJECT) {
            lr.setStatus(LeaveStatus.REJECTED);
        } else {
            throw new IllegalArgumentException("Unsupported decision action");
        }

        lr.setManagerNote(comment);
        lr.setDecidedBy(actorId);
        lr.setDecidedAt(LocalDateTime.now());
        lr.setUpdatedAt(LocalDateTime.now());

        LeaveRequest saved = leaveRequestRepository.save(lr);

        audit.log(actorId, "LEAVE_DECIDE", action.name() + " leave request id=" + id + " note=" + comment);

        return toDTO(saved);
    }

    // ----------------------------
    // DECIDE (bulk)
    // ----------------------------
    @Override
    @Transactional
    public BulkDecisionResultDTO decideMany(List<Long> ids, DecisionAction action, String comment, Long actorId) {
        List<Long> success = new ArrayList<>();
        List<Long> failed = new ArrayList<>();

        for (Long id : ids) {
            try {
                decide(id, action, comment, actorId);
                success.add(id);
            } catch (Exception ex) {
                failed.add(id);
            }
        }

        audit.log(actorId, "LEAVE_DECIDE_BULK", action.name() + " ids=" + ids.toString());

        return new BulkDecisionResultDTO(success, failed);
    }

    // ----------------------------
    // DTO Mapper
    // ----------------------------
    private LeaveRequestDTO toDTO(LeaveRequest req) {
        LeaveRequestDTO dto = new LeaveRequestDTO();

        dto.setId(req.getId());
        dto.setEmployeeId(req.getEmployee().getId());
        dto.setEmployeeCode(req.getEmployee().getCode());
        dto.setEmployeeName(req.getEmployee().getFullName());
        dto.setDepartment(req.getEmployee().getDepartment());

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
