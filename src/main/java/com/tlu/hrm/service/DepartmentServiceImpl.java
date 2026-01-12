package com.tlu.hrm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tlu.hrm.dto.DepartmentCreateDTO;
import com.tlu.hrm.dto.DepartmentDTO;
import com.tlu.hrm.dto.DepartmentUpdateDTO;
import com.tlu.hrm.entities.Department;
import com.tlu.hrm.repository.DepartmentRepository;
import com.tlu.hrm.repository.EmployeeRepository;

@Service
public class DepartmentServiceImpl implements DepartmentService{

	private final DepartmentRepository departmentRepository;
	private final EmployeeRepository employeeRepository;

	public DepartmentServiceImpl(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
		super();
		this.departmentRepository = departmentRepository;
		this.employeeRepository = employeeRepository;
	}
	
	// ================= CREATE =================
    @Override
    public DepartmentDTO create(DepartmentCreateDTO dto) {

        if (departmentRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new RuntimeException("Department name already exists");
        }

        Department department = new Department();
        department.setName(dto.getName());
        department.setDescription(dto.getDescription());
        department.setCode(generateDepartmentCode());
        department.setActive(true);

        return mapToDTO(departmentRepository.save(department));
    }

    // ================= UPDATE =================
    @Override
    public DepartmentDTO update(Long id, DepartmentUpdateDTO dto) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        if (dto.getName() != null && !dto.getName().isBlank()) {

            boolean exists = departmentRepository.existsByNameIgnoreCase(dto.getName());
            if (exists && !department.getName().equalsIgnoreCase(dto.getName())) {
                throw new RuntimeException("Department name already exists");
            }

            department.setName(dto.getName());
        }
        
        if (dto.getDescription() != null) {
            department.setDescription(dto.getDescription());
        }

        if (dto.getActive() != null) {
            if (!dto.getActive()) {
                boolean hasEmployees = employeeRepository.existsByDepartmentId(id);
                if (hasEmployees) {
                    throw new IllegalStateException(
                        "Không thể vô hiệu hóa phòng ban vì vẫn còn nhân viên đang thuộc phòng ban này"
                    );
                }
            }
            department.setActive(dto.getActive());
        }

        return mapToDTO(departmentRepository.save(department));
    }

    // ================= DELETE (SOFT) =================
    @Override
    public void delete(Long id) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng ban"));

        boolean hasEmployees = employeeRepository.existsByDepartmentId(id);
        if (hasEmployees) {
            throw new IllegalStateException(
                "Không thể vô hiệu hóa phòng ban vì vẫn còn nhân viên đang thuộc phòng ban này"
            );
        }
        
        department.setActive(false);
        departmentRepository.save(department);
    }

    // ================= GET ALL =================
    @Override
    public Page<DepartmentDTO> getAll(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return departmentRepository.findAll(pageable)
                .map(this::mapToDTO);
    }

    // ================= HELPER =================
    private String generateDepartmentCode() {

        String maxCode = departmentRepository.findMaxDepartmentCode();

        if (maxCode == null) {
            return "DEP001";
        }

        int next = Integer.parseInt(maxCode.substring(3)) + 1;
        return String.format("DEP%03d", next);
    }

    // ================= MAP DTO =================
    private DepartmentDTO mapToDTO(Department department) {

        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setCode(department.getCode());
        dto.setName(department.getName());
        dto.setDescription(department.getDescription());
        dto.setActive(department.isActive());
        return dto;
    }

}
