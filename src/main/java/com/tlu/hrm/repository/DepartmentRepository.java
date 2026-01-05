package com.tlu.hrm.repository;

import java.util.Optional;

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
}
