package com.tlu.hrm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tlu.hrm.dto.MySalaryListItemDTO;
import com.tlu.hrm.dto.MySalaryResponseDTO;

public interface SalaryService {

	MySalaryResponseDTO getMySalary(int month, int year);
	
	Page<MySalaryListItemDTO> getMySalaryList(Integer month, Integer year, Pageable pageable);
}
