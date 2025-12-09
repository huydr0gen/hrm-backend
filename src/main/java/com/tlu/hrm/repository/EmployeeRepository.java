package com.tlu.hrm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tlu.hrm.entities.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	
	boolean existsByCode(String code);
	
	List<Employee> findByUserIsNull();

	Page<Employee> findByUserIsNull(Pageable pageable);
	
	Optional<Employee> findByUserId(Long userId);
}
