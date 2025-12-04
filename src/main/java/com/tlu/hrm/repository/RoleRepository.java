package com.tlu.hrm.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tlu.hrm.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Optional<Role> findByName(String roleName);
	
	Set<Role> findByNameIn(Set<String> names);
}
