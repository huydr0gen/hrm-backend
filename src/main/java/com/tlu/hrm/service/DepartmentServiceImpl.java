package com.tlu.hrm.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tlu.hrm.dto.DepartmentCreateDTO;
import com.tlu.hrm.dto.DepartmentDTO;
import com.tlu.hrm.dto.DepartmentUpdateDTO;
import com.tlu.hrm.entities.Department;
import com.tlu.hrm.repository.DepartmentRepository;

@Service
public class DepartmentServiceImpl implements DepartmentService{

	private final DepartmentRepository departmentRepository;

	public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
		super();
		this.departmentRepository = departmentRepository;
	}
	
	// ================= CREATE =================

    @Override
    public DepartmentDTO create(DepartmentCreateDTO dto) {

        if (departmentRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new RuntimeException("Department name already exists");
        }

        Department department = new Department();
        department.setName(dto.getName());
        department.setActive(true);

        Department saved = departmentRepository.save(department);
        return mapToDTO(saved);
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

        if (dto.getActive() != null) {
            department.setActive(dto.getActive());
        }

        Department updated = departmentRepository.save(department);
        return mapToDTO(updated);
    }

    // ================= DELETE (SOFT) =================

    @Override
    public void delete(Long id) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        department.setActive(false);
        departmentRepository.save(department);
    }

    // ================= GET ALL ACTIVE =================

    @Override
    public List<DepartmentDTO> getAllActive() {

        return departmentRepository.findAll()
                .stream()
                .filter(Department::isActive)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ================= MAP DTO =================

    private DepartmentDTO mapToDTO(Department department) {

        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setActive(department.isActive());

        return dto;
    }
}
