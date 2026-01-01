package com.tlu.hrm.service;

import org.springframework.data.domain.Page;

import com.tlu.hrm.dto.EmployeeCertificateCreateDTO;
import com.tlu.hrm.dto.EmployeeCertificateResponseDTO;
import com.tlu.hrm.dto.EmployeeCertificateUpdateDTO;

public interface EmployeeCertificateService {

	// ===== HR =====
    EmployeeCertificateResponseDTO create(EmployeeCertificateCreateDTO dto);

    EmployeeCertificateResponseDTO update(Long id, EmployeeCertificateUpdateDTO dto);

    void delete(Long id);

    Page<EmployeeCertificateResponseDTO> listAll(
            int page, int size, String sort
    );

    Page<EmployeeCertificateResponseDTO> search(
            String keyword, int page, int size, String sort
    );

    EmployeeCertificateResponseDTO getDetail(Long id);

    // ===== EMPLOYEE =====
    Page<EmployeeCertificateResponseDTO> getMyCertificates(
            Long userId, int page, int size
    );

    EmployeeCertificateResponseDTO getMyCertificateDetail(
            Long userId, Long id
    );

    Page<EmployeeCertificateResponseDTO> getByEmployee(
            Long employeeId, int page, int size
    );
}
