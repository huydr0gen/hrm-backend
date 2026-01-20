package com.tlu.hrm.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tlu.hrm.dto.EmployeeCertificateCreateDTO;
import com.tlu.hrm.dto.EmployeeCertificateResponseDTO;
import com.tlu.hrm.dto.EmployeeCertificateUpdateDTO;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.entities.EmployeeCertificate;
import com.tlu.hrm.enums.CertificateStatus;
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
    
	// =====================================================
    // HR
    // =====================================================

	@Override
    public EmployeeCertificateResponseDTO create(EmployeeCertificateCreateDTO dto) {

        Employee employee;

        if (dto.getEmpCode() != null && !dto.getEmpCode().isBlank()) {
            employee = employeeRepo.findByCode(dto.getEmpCode())
                    .orElseThrow(() ->
                            new RuntimeException("Employee not found with code: " + dto.getEmpCode())
                    );
        } else if (dto.getEmployeeId() != null) {
            employee = employeeRepo.findById(dto.getEmployeeId())
                    .orElseThrow(() ->
                            new RuntimeException("Employee not found with id: " + dto.getEmployeeId())
                    );
        } else {
            throw new RuntimeException("empCode or employeeId is required");
        }

        EmployeeCertificate cert = new EmployeeCertificate();
        cert.setEmployee(employee);
        cert.setName(dto.getName());
        cert.setIssuer(dto.getIssuer());
        cert.setIssuedDate(dto.getIssuedDate());
        cert.setExpiredDate(dto.getExpiredDate());
        cert.setNote(dto.getNote());

        updateStatusByExpiredDate(cert);

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

        updateStatusByExpiredDate(cert);

        return toDTO(certificateRepo.save(cert));
    }

    @Override
    public void delete(Long id) {
        certificateRepo.deleteById(id);
    }

    @Override
    public Page<EmployeeCertificateResponseDTO> listAll(int page, int size, String sort) {
        syncExpiredStatuses();

        Pageable pageable = PageRequest.of(page, size, parseSort(sort));

        return certificateRepo.findAll(pageable)
                .map(this::toDTO);
    }

    @Override
    public Page<EmployeeCertificateResponseDTO> search(String keyword, int page, int size, String sort) {
        syncExpiredStatuses();

        Pageable pageable = PageRequest.of(page, size, parseSort(sort));

        return certificateRepo.searchByEmployee(keyword, pageable)
                .map(this::toDTO);
    }

    @Override
    public EmployeeCertificateResponseDTO getDetail(Long id) {

        EmployeeCertificate cert = certificateRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        return toDTO(cert);
    }

    // =====================================================
    // FILTER BY STATUS
    // =====================================================

    public Page<EmployeeCertificateResponseDTO> listByStatus(
            CertificateStatus status, int page, int size, String sort) {

        syncExpiredStatuses();

        Pageable pageable = PageRequest.of(page, size, parseSort(sort));

        return certificateRepo.findByStatus(status, pageable)
                .map(this::toDTO);
    }

    public Page<EmployeeCertificateResponseDTO> listByEmployeeAndStatus(
            Long employeeId, CertificateStatus status, int page, int size) {

        syncExpiredStatuses();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return certificateRepo.findByEmployeeIdAndStatus(employeeId, status, pageable)
                .map(this::toDTO);
    }

    // =====================================================
    // EMPLOYEE
    // =====================================================

    @Override
    public Page<EmployeeCertificateResponseDTO> getMyCertificates(Long userId, int page, int size) {
        syncExpiredStatuses();

        Employee emp = employeeRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return certificateRepo.findByEmployeeId(emp.getId(), pageable)
                .map(this::toDTO);
    }

    @Override
    public EmployeeCertificateResponseDTO getMyCertificateDetail(
            Long userId, Long id) {

        Employee emp = employeeRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeCertificate cert = certificateRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        if (!cert.getEmployee().getId().equals(emp.getId())) {
            throw new RuntimeException("Access denied");
        }

        return toDTO(cert);
    }
    
    @Override
    public Page<EmployeeCertificateResponseDTO> getByEmployee(Long employeeId, int page, int size) {
        syncExpiredStatuses();

        Pageable pageable = PageRequest.of(page, size);

        return certificateRepo.findByEmployeeId(employeeId, pageable)
                .map(this::toDTO);
    }

    // =====================================================
    // AUTO EXPIRE (CRONJOB)
    // =====================================================

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Ho_Chi_Minh")
    @Transactional
    public void autoExpireCertificates() {
        System.out.println(">>> AUTO EXPIRE JOB RUNNING <<<");

        List<EmployeeCertificate> list = certificateRepo.findCertificatesToExpire();

        System.out.println("Found: " + list.size() + " certificates to expire");

        for (EmployeeCertificate c : list) {
            c.setStatus(CertificateStatus.EXPIRED);
        }

        certificateRepo.saveAll(list);
    }
    
    @Transactional
    public void syncExpiredStatuses() {
        List<EmployeeCertificate> list = certificateRepo.findCertificatesToExpire();
        if (list.isEmpty()) return;

        for (EmployeeCertificate c : list) {
            c.setStatus(CertificateStatus.EXPIRED);
        }

        certificateRepo.saveAll(list);
    }

    // =====================================================
    // COMMON
    // =====================================================

    private void updateStatusByExpiredDate(EmployeeCertificate cert) {
        if (cert.getExpiredDate() != null
                && !cert.getExpiredDate().isAfter(LocalDate.now())) {
            cert.setStatus(CertificateStatus.EXPIRED);
        } else {
            cert.setStatus(CertificateStatus.ACTIVE);
        }
    }

    private Sort parseSort(String sort) {
        String[] arr = sort.split(",");
        return Sort.by(
                "desc".equalsIgnoreCase(arr[1])
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                arr[0]
        );
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
        dto.setStatus(c.getStatus()); // QUAN TRỌNG
        dto.setNote(c.getNote());
        return dto;
    }
}
