package com.tlu.hrm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tlu.hrm.dto.EmployeeCreateDTO;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.repository.EmployeeRepository;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Override
    public Employee createEmployee(EmployeeCreateDTO dto) {
		Employee employee = new Employee();
        employee.setCode(dto.getCode());
        employee.setFullName(dto.getFullName());
        employee.setDateOfBirth(dto.getDateOfBirth());
        employee.setPosition(dto.getPosition());
        employee.setDepartment(dto.getDepartment());
        employee.setEmail(dto.getEmail());
        employee.setPhoneNumber(dto.getPhoneNumber());

        return employeeRepository.save(employee);
	}
	
	@Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
	
	@Override
    public List<Employee> getEmployeesWithoutUser() {
        return employeeRepository.findByUserIsNull();
    }
	
	@Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

}
