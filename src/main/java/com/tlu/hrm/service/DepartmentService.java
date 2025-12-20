package com.tlu.hrm.service;

import java.util.List;

import com.tlu.hrm.dto.DepartmentCreateDTO;
import com.tlu.hrm.dto.DepartmentDTO;
import com.tlu.hrm.dto.DepartmentUpdateDTO;

public interface DepartmentService {

	DepartmentDTO create(DepartmentCreateDTO dto);

    DepartmentDTO update(Long id, DepartmentUpdateDTO dto);

    void delete(Long id);

    List<DepartmentDTO> getAllActive();
}
