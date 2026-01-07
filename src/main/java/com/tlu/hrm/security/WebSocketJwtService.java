package com.tlu.hrm.security;

import org.springframework.stereotype.Component;

import com.tlu.hrm.entities.User;
import com.tlu.hrm.repository.UserRepository;

@Component
public class WebSocketJwtService {

	private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    public WebSocketJwtService(JwtProvider jwtProvider, UserRepository userRepository) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }

    public boolean validateToken(String token) {
        return jwtProvider.validateToken(token);
    }

    public Long getEmployeeId(String token) {

        String username = jwtProvider.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + username)
                );

        if (user.getEmployee() == null) {
            throw new RuntimeException("User has no employee linked");
        }

        return user.getEmployee().getId();
    }
}
