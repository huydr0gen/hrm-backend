package com.tlu.hrm.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tlu.hrm.entities.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

	boolean existsByNameIgnoreCase(String name);

    boolean existsByCode(String code);
    
    Optional<Department> findByCode(String code);

    @Query("""
        SELECT MAX(d.code)
        FROM Department d
        WHERE d.code LIKE 'DEP%'
    """)
    String findMaxDepartmentCode();
    
    long countByActiveTrue();
    
    long countByActiveFalse();
    
    @Query(
	    value = """
	        SELECT *
	        FROM departments d
	        ORDER BY
	          CASE
	            WHEN d.active = true THEN 1
	            ELSE 2
	          END,
	          CAST(SUBSTR(d.code, 4) AS INTEGER) ASC
	    """,
	    countQuery = "SELECT COUNT(*) FROM departments",
	    nativeQuery = true
	)
	Page<Department> findAllSortedByActiveAndCodeNumeric(Pageable pageable);
}
