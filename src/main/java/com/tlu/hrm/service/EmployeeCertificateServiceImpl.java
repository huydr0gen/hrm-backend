package com.tlu.hrm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tlu.hrm.dto.EmployeeCertificateCreateDTO;
import com.tlu.hrm.dto.EmployeeCertificateResponseDTO;
import com.tlu.hrm.dto.EmployeeCertificateUpdateDTO;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.entities.EmployeeCertificate;
import com.tlu.hrm.repository.EmployeeCertificateRepository;
import com.tlu.hrm.repository.EmployeeRepository;

@Service
public class EmployeeCertificateServiceImpl implements EmployeeCertificateService{

	private final EmployeeCertificateRepository certificateRepo;
    private final EmployeeRepository employeeRepo;
    
	public EmployeeCertificateServiceImpl(EmployeeCertificateRepository certificateRepo,
			EmployeeRepository employeeRepo) {
		super();
		this.certificateRepo = certificateRepo;
		this.employeeRepo = employeeRepo;
	}
    
	@Override
    public EmployeeCertificateResponseDTO create(EmployeeCertificateCreateDTO dto) {
        Employee employee = employeeRepo.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeCertificate cert = new EmployeeCertificate();
        cert.setEmployee(employee);
        cert.setName(dto.getName());
        cert.setIssuer(dto.getIssuer());
        cert.setIssuedDate(dto.getIssuedDate());
        cert.setExpiredDate(dto.getExpiredDate());
        cert.setNote(dto.getNote());

        return toDTO(certificateRepo.save(cert));
    }

    @Override
    public EmployeeCertificateResponseDTO update(Long id, EmployeeCertificateUpdateDTO dto) {
        EmployeeCertificate cert = certificateRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        cert.setName(dto.getName());
        cert.setIssuer(dto.getIssuer());
        cert.setIssuedDate(dto.getIssuedDate());
        cert.setExpiredDate(dto.getExpiredDate());
        cert.setNote(dto.getNote());

        return toDTO(certificateRepo.save(cert));
    }

    @Override
    public void delete(Long id) {
        certificateRepo.deleteById(id);
    }

    @Override
    public Page<EmployeeCertificateResponseDTO> getByEmployee(
            Long employeeId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return certificateRepo.findByEmployeeId(employeeId, pageable)
                .map(this::toDTO);
    }

    @Override
    public Page<EmployeeCertificateResponseDTO> getMyCertificates(
            Long userId, int page, int size) {

        Employee emp = employeeRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return getByEmployee(emp.getId(), page, size);
    }

    @Override
    public EmployeeCertificateResponseDTO getById(Long id) {
        return toDTO(
                certificateRepo.findById(id)
                        .orElseThrow(() -> new RuntimeException("Certificate not found"))
        );
    }
    
    @Override
    public EmployeeCertificateResponseDTO getDetail(Long id) {
        EmployeeCertificate cert = certificateRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        return toDTO(cert);
    }

    @Override
    public EmployeeCertificateResponseDTO getMyCertificateDetail(Long userId, Long id) {

        Employee emp = employeeRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeCertificate cert = certificateRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        if (!cert.getEmployee().getId().equals(emp.getId())) {
            throw new RuntimeException("Access denied");
        }

        return toDTO(cert);
    }

    private EmployeeCertificateResponseDTO toDTO(EmployeeCertificate c) {
        EmployeeCertificateResponseDTO dto = new EmployeeCertificateResponseDTO();
        dto.setId(c.getId());
        dto.setEmployeeCode(c.getEmployee().getCode());
        dto.setEmployeeName(c.getEmployee().getFullName());
        dto.setName(c.getName());
        dto.setIssuer(c.getIssuer());
        dto.setIssuedDate(c.getIssuedDate());
        dto.setExpiredDate(c.getExpiredDate());
        dto.setNote(c.getNote());
        return dto;
    }

}
