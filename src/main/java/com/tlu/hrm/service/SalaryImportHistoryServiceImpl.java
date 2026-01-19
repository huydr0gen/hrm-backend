package com.tlu.hrm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tlu.hrm.dto.SalaryImportHistoryResponseDTO;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.entities.SalaryImportHistory;
import com.tlu.hrm.repository.EmployeeRepository;
import com.tlu.hrm.repository.SalaryImportHistoryRepository;
import com.tlu.hrm.security.CustomUserDetails;

@Service
@Transactional
public class SalaryImportHistoryServiceImpl implements SalaryImportHistoryService {

	private final SalaryImportHistoryRepository historyRepo;
    private final EmployeeRepository employeeRepo;
    
	public SalaryImportHistoryServiceImpl(SalaryImportHistoryRepository historyRepo, EmployeeRepository employeeRepo) {
		super();
		this.historyRepo = historyRepo;
		this.employeeRepo = employeeRepo;
	}
    
	@Override
    public Page<SalaryImportHistoryResponseDTO> getByMonth(
            String month,
            int page,
            int size) {

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return historyRepo.findByMonth(month, pageable)
                .map(this::toDTO);
    }

    @Override
    public void createHistory(String month, String fileName, String filePath) {

        Long createdById = getCurrentEmployeeId();

        Employee creator = employeeRepo.findById(createdById)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        SalaryImportHistory h = new SalaryImportHistory();
        h.setMonth(month);
        h.setFileName(fileName);
        h.setFilePath(filePath);
        h.setCreatedBy(creator);

        historyRepo.save(h);
    }
    
    @Override
    public Page<SalaryImportHistoryResponseDTO> getAll(int page, int size) {

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return historyRepo.findAll(pageable)
                .map(this::toDTO);
    }

    private Long getCurrentEmployeeId() {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            throw new RuntimeException("Unauthorized");
        }

        CustomUserDetails ud = (CustomUserDetails) auth.getPrincipal();

        Employee emp = employeeRepo.findByUserId(ud.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        return emp.getId();
    }

    private SalaryImportHistoryResponseDTO toDTO(SalaryImportHistory h) {

        SalaryImportHistoryResponseDTO dto = new SalaryImportHistoryResponseDTO();

        dto.setId(h.getId());
        dto.setMonth(h.getMonth());
        dto.setFileName(h.getFileName());
        dto.setFilePath(h.getFilePath());
        dto.setCreatedAt(h.getCreatedAt());

        dto.setCreatedById(h.getCreatedBy().getId());
        dto.setCreatedByCode(h.getCreatedBy().getCode());
        dto.setCreatedByName(h.getCreatedBy().getFullName());

        return dto;
    }
    
}
