package com.tlu.hrm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tlu.hrm.entities.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

	boolean existsByNameIgnoreCase(String name);
}
