package com.tlu.hrm.service;

import com.tlu.hrm.dto.MySalaryResponseDTO;

public interface SalaryService {

	MySalaryResponseDTO getMySalary(int month, int year);
}
