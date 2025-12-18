package com.tlu.hrm.service;

import org.springframework.data.domain.Page;

import com.tlu.hrm.dto.EmployeeCreateDTO;
import com.tlu.hrm.dto.EmployeeDTO;
import com.tlu.hrm.dto.EmployeeUpdateDTO;

public interface EmployeeService {
	
	EmployeeDTO createEmployee(EmployeeCreateDTO dto);

    Page<EmployeeDTO> getAllEmployees(int page, int size);

    EmployeeDTO getEmployeeById(Long id);

    Page<EmployeeDTO> getEmployeesWithoutUser(int page, int size);

    EmployeeDTO updateEmployee(Long id, EmployeeUpdateDTO dto);

    void deleteEmployee(Long id);
    
    Page<EmployeeDTO> getEmployeesOfMyDepartment(int page, int size);


}
