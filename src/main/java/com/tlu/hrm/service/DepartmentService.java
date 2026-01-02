package com.tlu.hrm.service;

import org.springframework.data.domain.Page;

import com.tlu.hrm.dto.DepartmentCreateDTO;
import com.tlu.hrm.dto.DepartmentDTO;
import com.tlu.hrm.dto.DepartmentUpdateDTO;

public interface DepartmentService {

	DepartmentDTO create(DepartmentCreateDTO dto);

    DepartmentDTO update(Long id, DepartmentUpdateDTO dto);

    void delete(Long id);

    Page<DepartmentDTO> getAll(int page, int size);
}
