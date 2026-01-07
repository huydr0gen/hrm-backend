package com.tlu.hrm.security;

import org.springframework.stereotype.Component;

import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.repository.EmployeeRepository;

@Component
public class WebSocketJwtService {

	private final JwtProvider jwtProvider;
    private final EmployeeRepository employeeRepository;
    
    public WebSocketJwtService(JwtProvider jwtProvider, EmployeeRepository employeeRepository) {
        this.jwtProvider = jwtProvider;
        this.employeeRepository = employeeRepository;
    }
    
    public boolean validateToken(String token) {
        return jwtProvider.validateToken(token);
    }

    public Long getUserId(String token) {

        // 1. Lấy username từ JWT
        String username = jwtProvider.extractUsername(token);

        // 2. Tìm employee theo username
        Employee employee = employeeRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + username)
                );

        // 3. Trả về employee.id
        return employee.getId();
    }
}
