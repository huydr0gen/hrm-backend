package com.tlu.hrm.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.tlu.hrm.dto.UserCreateDTO;
import com.tlu.hrm.dto.UserUpdateDTO;
import com.tlu.hrm.entities.User;

public interface UserService {
	
	User createUserFromEmployee(Long employeeId);

    User createUser(UserCreateDTO dto);

    User updateUser(Long id, UserUpdateDTO dto);

    void deleteUser(Long id);

    User getUserById(Long id);

    List<User> getAllUsers();

    User assignRoles(Long userId, Set<String> roleNames);

    User resetPassword(Long userId);

    void activateUser(Long id);      // ACTIVE

    void deactivateUser(Long id);    // INACTIVE

    void lockUser(Long id);          // LOCKED

    boolean existsByUsername(String username);
    
    void updateRefreshToken(Long userId, String refreshToken);
    
    Page<User> getUsers(int page, int size);
    
    User getUserByUsername(String username);

}
