package com.tlu.hrm.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tlu.hrm.entities.Role;
import com.tlu.hrm.repository.RoleRepository;

@Service
public class RoleServiceImpl implements RoleService {
	
	private final RoleRepository roleRepository;

	public RoleServiceImpl(RoleRepository roleRepository) {
		super();
		this.roleRepository = roleRepository;
	}
	
	@Override
	public Role createRole(Role role) {
		return roleRepository.save(role);
	}
	
	@Override
	public Role updateRole(Long id, Role role) {
		return roleRepository.findById(id).map(r -> {
            r.setName(role.getName());
            r.setDescription(role.getDescription());
            return roleRepository.save(r);
        }).orElseThrow(() -> new RuntimeException("Role not found"));
	}
	
	@Override
	public void deleteRole(Long id) {
		roleRepository.deleteById(id);
	}
	
	@Override
	public Role getRoleById(Long id) {
		return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
	}
    
	@Override
	public List<Role> getAllRoles() {
		return roleRepository.findAll();
	}
    
	@Override
	public Role getRoleByName(String name) {
		return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found"));
	}
	
}
