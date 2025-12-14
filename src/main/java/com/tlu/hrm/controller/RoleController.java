package com.tlu.hrm.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tlu.hrm.entities.Role;
import com.tlu.hrm.service.RoleService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/roles")
public class RoleController {

	private final RoleService roleService;

	public RoleController(RoleService roleService) {
		super();
		this.roleService = roleService;
	}
	
	@PostMapping("path")
	public ResponseEntity<Role> createRole(@RequestBody Role role) {
		return ResponseEntity.ok(roleService.createRole(role));
	}
	
	@GetMapping
	public ResponseEntity<List<Role>> getAllRoles() {
		return ResponseEntity.ok(roleService.getAllRoles());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
		return ResponseEntity.ok(roleService.getRoleById(id));
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<Role> getRoleByName(@PathVariable String name) {
		return ResponseEntity.ok(roleService.getRoleByName(name));
	}
	
	@PutMapping
	public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody Role role) {
		return ResponseEntity.ok(roleService.updateRole(id, role));
	}
	
	@DeleteMapping
	public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
		roleService.deleteRole(id);
		return ResponseEntity.noContent().build();
	}
}
