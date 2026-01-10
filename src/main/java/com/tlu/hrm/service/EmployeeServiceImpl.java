package com.tlu.hrm.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tlu.hrm.dto.EmployeeCreateDTO;
import com.tlu.hrm.dto.EmployeeDTO;
import com.tlu.hrm.dto.EmployeeUpdateDTO;
import com.tlu.hrm.entities.Department;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.repository.DepartmentRepository;
import com.tlu.hrm.repository.EmployeeRepository;
import com.tlu.hrm.security.CustomUserDetails;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	private EmployeeRepository employeeRepository;
	private final DepartmentRepository departmentRepository;
	
	public EmployeeServiceImpl(EmployeeRepository employeeRepository, 
			DepartmentRepository departmentRepository) {
		super();
		this.employeeRepository = employeeRepository;
		this.departmentRepository = departmentRepository;
	}
	
	// ================= CREATE =================
	@Override
	public EmployeeDTO createEmployee(EmployeeCreateDTO dto) {

	    // ===== CHECK CITIZEN ID =====
		if (dto.getCitizenId() != null && !dto.getCitizenId().isBlank()) {
		    if (employeeRepository.existsByCitizenId(dto.getCitizenId())) {
		        throw new RuntimeException("Citizen ID already exists");
		    }
		}

	    Department department = departmentRepository.findById(dto.getDepartmentId())
	            .orElseThrow(() -> new RuntimeException("Department not found"));

	    Employee emp = new Employee();

	    // AUTO CODE
	    emp.setCode(generateEmployeeCode());

	    emp.setFullName(dto.getFullName());
	    emp.setDateOfBirth(dto.getDateOfBirth());
	    emp.setGender(dto.getGender());         
	    emp.setCitizenId(dto.getCitizenId());   
	    emp.setAddress(dto.getAddress());        

	    emp.setPosition(dto.getPosition());
	    emp.setDepartment(department);

	    if (dto.getOnboardDate() != null) {
	        emp.setOnboardDate(dto.getOnboardDate());
	    } else {
	        emp.setOnboardDate(LocalDate.now());
	    }

	    emp.setEmail(null);
	    emp.setPhoneNumber(dto.getPhoneNumber());

	    Employee saved = employeeRepository.save(emp);
	    return mapToDTO(saved);
	}

    // =====================================================
    // 🔹 SUPPORT METHOD: GENERATE EMPLOYEE CODE
    // =====================================================
    private String generateEmployeeCode() {

        String maxCode = employeeRepository.findMaxEmployeeCode();

        // DB chưa có EMP nào
        if (maxCode == null) {
            return "EMP001";
        }

        // Bỏ prefix EMP, parse số
        int nextNumber;
        try {
            nextNumber = Integer.parseInt(maxCode.replace("EMP", "")) + 1;
        } catch (NumberFormatException e) {
            // Fallback an toàn (không nên xảy ra)
            nextNumber = 1;
        }

        // Không giới hạn 3 chữ số → hỗ trợ EMP1000+
        return "EMP" + nextNumber;
    }

    // ================= GET ALL =================
    @Override
    public Page<EmployeeDTO> getAllEmployees(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employees = employeeRepository.findAll(pageable);

        List<EmployeeDTO> dtos = employees.getContent()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, employees.getTotalElements());
    }

    // ================= GET BY ID =================
    @Override
    public EmployeeDTO getEmployeeById(Long id) {

        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return mapToDTO(emp);
    }

    // ================= WITHOUT USER =================
    @Override
    public Page<EmployeeDTO> getEmployeesWithoutUser(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employees = employeeRepository.findByUserIsNull(pageable);

        return employees.map(this::mapToDTO);
    }

    // ================= UPDATE =================
    @Override
    public EmployeeDTO updateEmployee(Long id, EmployeeUpdateDTO dto) {

        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (dto.getFullName() != null) emp.setFullName(dto.getFullName());
        if (dto.getDateOfBirth() != null) emp.setDateOfBirth(dto.getDateOfBirth());
        
        if (dto.getGender() != null)
            emp.setGender(dto.getGender());

        if (dto.getCitizenId() != null) {

            if (!dto.getCitizenId().isBlank()) {
                if (employeeRepository.existsByCitizenIdAndIdNot(dto.getCitizenId(), id)) {
                    throw new RuntimeException("Citizen ID already exists");
                }
                emp.setCitizenId(dto.getCitizenId());
            } else {
                emp.setCitizenId(null);
            }
        }
        
        if (dto.getAddress() != null)
            emp.setAddress(dto.getAddress());
        
        if (dto.getPosition() != null) emp.setPosition(dto.getPosition());

        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            emp.setDepartment(department);
        }
        
        if (dto.getOnboardDate() != null)
            emp.setOnboardDate(dto.getOnboardDate());

        if (dto.getPhoneNumber() != null)
            emp.setPhoneNumber(dto.getPhoneNumber());

        Employee updated = employeeRepository.save(emp);
        return mapToDTO(updated);
    }

    // ================= DELETE =================
    @Override
    public void deleteEmployee(Long id) {

        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found");
        }

        employeeRepository.deleteById(id);
    }

    // ================= MY DEPARTMENT =================
    @Override
    public Page<EmployeeDTO> getEmployeesOfMyDepartment(int page, int size) {

        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (!(principal instanceof CustomUserDetails userDetails)) {
            throw new RuntimeException("Unauthenticated");
        }

        if (userDetails.getEmployeeId() == null) {
            throw new RuntimeException("Manager has no employee profile");
        }

        Employee manager = employeeRepository.findById(userDetails.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Manager employee not found"));

        Department department = manager.getDepartment();

        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employees =
                employeeRepository.findByDepartment(department, pageable);

        return employees.map(this::mapToDTO);
    }
    
    // ================= MY PROFILE =================
    @Override
    public EmployeeDTO getMyProfile() {

        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (!(principal instanceof CustomUserDetails userDetails)) {
            throw new RuntimeException("Unauthenticated");
        }

        if (userDetails.getEmployeeId() == null) {
            throw new RuntimeException("User has no employee profile");
        }

        Employee emp = employeeRepository.findById(userDetails.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return mapToDTO(emp);
    }

    // ================= MAP DTO =================
    private EmployeeDTO mapToDTO(Employee emp) {

        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(emp.getId());
        dto.setCode(emp.getCode());
        dto.setFullName(emp.getFullName());
        dto.setDateOfBirth(emp.getDateOfBirth());
        dto.setGender(emp.getGender());
        dto.setCitizenId(emp.getCitizenId());
        dto.setAddress(emp.getAddress());
        dto.setPosition(emp.getPosition());
        dto.setDepartmentId(emp.getDepartment().getId());
        dto.setDepartmentName(emp.getDepartment().getName());
        dto.setOnboardDate(emp.getOnboardDate());
        dto.setStatus(emp.getStatus());
        dto.setEmail(emp.getEmail());
        dto.setPhoneNumber(emp.getPhoneNumber());

        if (emp.getUser() != null) {
            dto.setUserId(emp.getUser().getId());
        }
        dto.setCreatedAt(emp.getCreatedAt());
        
        return dto;
    }
}
