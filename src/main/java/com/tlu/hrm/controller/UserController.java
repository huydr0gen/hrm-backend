package com.tlu.hrm.controller;

import com.tlu.hrm.dto.UserCreateDTO;
import com.tlu.hrm.dto.UserDTO;
import com.tlu.hrm.dto.UserUpdateDTO;
import com.tlu.hrm.entities.User;
import com.tlu.hrm.service.UserService;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
		super();
		this.userService = userService;
	}
    
 // CREATE USER
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserCreateDTO dto) {
        return ResponseEntity.ok(mapToDto(userService.createUser(dto)));
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping("/from-employee/{employeeId}")
    public ResponseEntity<UserDTO> createUserFromEmployee(@PathVariable Long employeeId) {
        User user = userService.createUserFromEmployee(employeeId);
        return ResponseEntity.ok(mapToDto(user));
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/reset-password")
    public ResponseEntity<UserDTO> resetPassword(@PathVariable Long id) {
        User user = userService.resetPassword(id);
        return ResponseEntity.ok(mapToDto(user));
    }

    // GET USERS WITH PAGINATION
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<Page<UserDTO>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<User> users = userService.getUsers(page, size);

        Page<UserDTO> dtoPage = users.map(this::mapToDto);

        return ResponseEntity.ok(dtoPage);
    }

    // GET USER BY ID
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER','EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(mapToDto(user));
    }

    // UPDATE USER
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO dto) {
        User updateUser = userService.updateUser(id, dto);
        return ResponseEntity.ok(mapToDto(updateUser));
    }

    // DELETE USER
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/roles")
    public ResponseEntity<UserDTO> assignRoles(@PathVariable Long id, @RequestBody Set<String> roleNames) {
        User user = userService.assignRoles(id, roleNames);
        return ResponseEntity.ok(mapToDto(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/lock")
    public ResponseEntity<Void> lockUser(@PathVariable Long id) {
        userService.lockUser(id);
        return ResponseEntity.ok().build();
    }

    // GET CURRENT LOGGED-IN USER
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(mapToDto(user));
    }

    private UserDTO mapToDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());

        dto.setRoles(user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet()));

        dto.setStatus(user.getStatus());

        if (user.getEmployee() != null) {
            dto.setEmployeeId(user.getEmployee().getId());
        }

        dto.setLastLogin(user.getLastLogin());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        return dto;
    }
}
