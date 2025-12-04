package com.tlu.hrm.service;

import java.util.List;

import com.tlu.hrm.dto.EmployeeCreateDTO;
import com.tlu.hrm.entities.Employee;

public interface EmployeeService {
	
	Employee createEmployee(EmployeeCreateDTO dto);

    List<Employee> getAllEmployees();

    List<Employee> getEmployeesWithoutUser();

    Employee getEmployeeById(Long id);

}
