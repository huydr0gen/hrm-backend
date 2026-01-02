package com.tlu.hrm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tlu.hrm.dto.AttendanceImportHistoryResponseDTO;
import com.tlu.hrm.entities.AttendanceImportHistory;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.repository.AttendanceImportHistoryRepository;
import com.tlu.hrm.repository.EmployeeRepository;

@Service
@Transactional
public class AttendanceImportHistoryServiceImpl implements AttendanceImportHistoryService {

	private final AttendanceImportHistoryRepository historyRepo;
    private final EmployeeRepository employeeRepo;
	public AttendanceImportHistoryServiceImpl(AttendanceImportHistoryRepository historyRepo,
			EmployeeRepository employeeRepo) {
		super();
		this.historyRepo = historyRepo;
		this.employeeRepo = employeeRepo;
	}
    
	@Override
    public Page<AttendanceImportHistoryResponseDTO> getByMonth(
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
    public void createHistory(
            String month,
            String fileName,
            String filePath,
            Long createdById) {

        Employee creator = employeeRepo.findById(createdById)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        AttendanceImportHistory h = new AttendanceImportHistory();
        h.setMonth(month);
        h.setFileName(fileName);
        h.setFilePath(filePath);
        h.setCreatedBy(creator);

        historyRepo.save(h);
    }

    private AttendanceImportHistoryResponseDTO toDTO(AttendanceImportHistory h) {

        AttendanceImportHistoryResponseDTO dto =
                new AttendanceImportHistoryResponseDTO();

        dto.setId(h.getId());
        dto.setMonth(h.getMonth());
        dto.setFileName(h.getFileName());
        dto.setFilePath(h.getFilePath());
        dto.setCreatedAt(h.getCreatedAt());

        dto.setCreatedById(h.getCreatedBy().getId());
        dto.setCreatedByName(h.getCreatedBy().getFullName());

        return dto;
    }
}
