package com.tlu.hrm.service;

import org.springframework.data.domain.Page;

import com.tlu.hrm.dto.EmployeeCertificateCreateDTO;
import com.tlu.hrm.dto.EmployeeCertificateResponseDTO;
import com.tlu.hrm.dto.EmployeeCertificateUpdateDTO;

public interface EmployeeCertificateService {

	EmployeeCertificateResponseDTO create(EmployeeCertificateCreateDTO dto);

    EmployeeCertificateResponseDTO update(Long id, EmployeeCertificateUpdateDTO dto);

    void delete(Long id);

    Page<EmployeeCertificateResponseDTO> getByEmployee(
            Long employeeId, int page, int size
    );

    Page<EmployeeCertificateResponseDTO> getMyCertificates(
            Long userId, int page, int size
    );

    EmployeeCertificateResponseDTO getById(Long id);
    
    EmployeeCertificateResponseDTO getDetail(Long id);

    EmployeeCertificateResponseDTO getMyCertificateDetail(Long userId, Long id);
}
