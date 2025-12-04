package com.tlu.hrm.service;

import java.util.List;

import com.tlu.hrm.entities.Role;

public interface RoleService {

	Role createRole(Role role);
	Role updateRole(Long id, Role role);
	void deleteRole(Long id);
	Role getRoleById(Long id);
    List<Role> getAllRoles();
    Role getRoleByName(String name);
	
}
