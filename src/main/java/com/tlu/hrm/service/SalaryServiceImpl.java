package com.tlu.hrm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tlu.hrm.dto.MySalaryListItemDTO;
import com.tlu.hrm.dto.MySalaryResponseDTO;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.entities.SalaryRecord;
import com.tlu.hrm.repository.EmployeeRepository;
import com.tlu.hrm.repository.SalaryRecordRepository;
import com.tlu.hrm.security.CustomUserDetails;

@Service
public class SalaryServiceImpl implements SalaryService {

	private final SalaryRecordRepository salaryRepo;
    private final EmployeeRepository employeeRepo;
	
	public SalaryServiceImpl(SalaryRecordRepository salaryRepo, EmployeeRepository employeeRepo) {
		super();
		this.salaryRepo = salaryRepo;
		this.employeeRepo = employeeRepo;
	}

	@Override
    public MySalaryResponseDTO getMySalary(int month, int year) {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails user =
                (CustomUserDetails) auth.getPrincipal();

        Employee emp = employeeRepo.findByUserId(user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Employee not found"));

        Long empId = emp.getId();

        return salaryRepo
                .findByEmployeeIdAndMonthAndYear(empId, month, year)
                .map(MySalaryResponseDTO::available)
                .orElse(MySalaryResponseDTO.notAvailable(month, year));
    }
	
	@Override
	public Page<MySalaryListItemDTO> getMySalaryList(Integer month, Integer year, Pageable pageable) {

	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

	    Employee emp = employeeRepo.findByUserId(user.getId())
	            .orElseThrow(() -> new RuntimeException("Employee not found"));

	    Long empId = emp.getId();

	    Page<SalaryRecord> page;

	    if (month != null && year != null) {
	        page = salaryRepo.findByEmployeeIdAndMonthAndYear(empId, month, year, pageable);
	    } else {
	        page = salaryRepo.findByEmployeeId(empId, pageable);
	    }

	    return page.map(record -> {
	        MySalaryListItemDTO dto = new MySalaryListItemDTO();
	        dto.setMonth(record.getMonth());
	        dto.setYear(record.getYear());
	        dto.setTitle("Kỳ lương T" + record.getMonth() + "/" + record.getYear());
	        dto.setImportedAt(record.getImportedAt());
	        return dto;
	    });
	}
}
