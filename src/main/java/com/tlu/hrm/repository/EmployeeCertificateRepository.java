package com.tlu.hrm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tlu.hrm.entities.EmployeeCertificate;

public interface EmployeeCertificateRepository extends JpaRepository<EmployeeCertificate, Long> {

	Page<EmployeeCertificate> findByEmployeeId(Long employeeId, Pageable pageable);
}
