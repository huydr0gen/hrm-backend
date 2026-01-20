package com.tlu.hrm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tlu.hrm.entities.EmployeeCertificate;
import com.tlu.hrm.enums.CertificateStatus;

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
    
    Page<EmployeeCertificate> findByStatus(CertificateStatus status, Pageable pageable);
    
    Page<EmployeeCertificate> findByEmployeeIdAndStatus(
	    Long employeeId,
	    CertificateStatus status,
	    Pageable pageable
	);
    
    @Query("""
	SELECT c FROM EmployeeCertificate c
	WHERE c.status = 'ACTIVE'
	  AND c.expiredDate IS NOT NULL
	  AND c.expiredDate <= CURRENT_DATE
	""")
	List<EmployeeCertificate> findCertificatesToExpire();
}
