package com.tlu.hrm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tlu.hrm.entities.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	
	boolean existsByCode(String code);
	
	List<Employee> findByUserIsNull();

}
