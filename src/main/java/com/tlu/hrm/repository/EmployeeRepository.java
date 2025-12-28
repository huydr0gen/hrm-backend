package com.tlu.hrm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tlu.hrm.entities.Department;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.enums.EmployeeStatus;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	
	boolean existsByCode(String code);
	
	Optional<Employee> findByCode(String code);

    @Query("""
        SELECT MAX(e.code)
        FROM Employee e
        WHERE e.code LIKE 'EMP%'
    """)
    String findMaxEmployeeCode();

    List<Employee> findByUserIsNull();

    Page<Employee> findByUserIsNull(Pageable pageable);

    Optional<Employee> findByUserId(Long userId);

    List<Employee> findByDepartment(Department department);

    Page<Employee> findByDepartment(Department department, Pageable pageable);
    
    List<Employee> findAllByStatus(EmployeeStatus status);
}
