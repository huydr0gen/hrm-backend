package com.tlu.hrm.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tlu.hrm.dto.EmployeeCreateDTO;
import com.tlu.hrm.dto.EmployeeDTO;
import com.tlu.hrm.dto.EmployeeUpdateDTO;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.repository.EmployeeRepository;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	private EmployeeRepository employeeRepository;
	
	public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
		super();
		this.employeeRepository = employeeRepository;
	}

	@Override
    public EmployeeDTO createEmployee(EmployeeCreateDTO dto) {

        if (employeeRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Employee code already exists");
        }

        Employee emp = new Employee();
        emp.setCode(dto.getCode());
        emp.setFullName(dto.getFullName());
        emp.setDateOfBirth(dto.getDateOfBirth());
        emp.setPosition(dto.getPosition());
        emp.setDepartment(dto.getDepartment());
        emp.setEmail(dto.getEmail());
        emp.setPhoneNumber(dto.getPhoneNumber());

        Employee saved = employeeRepository.save(emp);
        return mapToDTO(saved);
    }

    @Override
    public Page<EmployeeDTO> getAllEmployees(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);

        Page<Employee> employees = employeeRepository.findAll(pageable);

        List<EmployeeDTO> dtos = employees.getContent()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, employees.getTotalElements());
    }

    @Override
    public EmployeeDTO getEmployeeById(Long id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return mapToDTO(emp);
    }

    @Override
    public Page<EmployeeDTO> getEmployeesWithoutUser(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);

        // Fetch all employees without user (not paginated)
        List<Employee> allNoUser = employeeRepository.findByUserIsNull();

        // Manual pagination
        int start = Math.min(pageable.getPageNumber() * pageable.getPageSize(), allNoUser.size());
        int end = Math.min(start + pageable.getPageSize(), allNoUser.size());

        List<EmployeeDTO> dtos = allNoUser.subList(start, end)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, allNoUser.size());
    }

    @Override
    public EmployeeDTO updateEmployee(Long id, EmployeeUpdateDTO dto) {

        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (dto.getFullName() != null) emp.setFullName(dto.getFullName());
        if (dto.getDateOfBirth() != null) emp.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getPosition() != null) emp.setPosition(dto.getPosition());
        if (dto.getDepartment() != null) emp.setDepartment(dto.getDepartment());
        if (dto.getEmail() != null) emp.setEmail(dto.getEmail());
        if (dto.getPhoneNumber() != null) emp.setPhoneNumber(dto.getPhoneNumber());

        Employee updated = employeeRepository.save(emp);
        return mapToDTO(updated);
    }

    @Override
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found");
        }
        employeeRepository.deleteById(id);
    }

    // -------------------------------
    // Mapping Entity → DTO
    // -------------------------------
    private EmployeeDTO mapToDTO(Employee emp) {

        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(emp.getId());
        dto.setCode(emp.getCode());
        dto.setFullName(emp.getFullName());
        dto.setDateOfBirth(emp.getDateOfBirth());
        dto.setPosition(emp.getPosition());
        dto.setDepartment(emp.getDepartment());
        dto.setEmail(emp.getEmail());
        dto.setPhoneNumber(emp.getPhoneNumber());

        if (emp.getUser() != null) {
            dto.setUserId(emp.getUser().getId());
        }

        return dto;
    }

}
