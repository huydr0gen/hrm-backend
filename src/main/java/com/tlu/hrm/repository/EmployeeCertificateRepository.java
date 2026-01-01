package com.tlu.hrm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tlu.hrm.entities.EmployeeCertificate;

public interface EmployeeCertificateRepository extends JpaRepository<EmployeeCertificate, Long> {

	Page<EmployeeCertificate> findByEmployeeId(Long employeeId, Pageable pageable);

    // ===== HR SEARCH =====
    @Query("""
        SELECT c FROM EmployeeCertificate c
        JOIN c.employee e
        WHERE 
            LOWER(e.code) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(e.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<EmployeeCertificate> searchByEmployee(
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
