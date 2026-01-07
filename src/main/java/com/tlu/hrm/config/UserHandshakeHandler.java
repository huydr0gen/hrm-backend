package com.tlu.hrm.config;

import java.security.Principal;
import java.util.Map;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.tlu.hrm.security.WebSocketJwtService;

public class UserHandshakeHandler extends DefaultHandshakeHandler{

	private final WebSocketJwtService jwtService;

    public UserHandshakeHandler(WebSocketJwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected Principal determineUser(
            org.springframework.http.server.ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {

        String token = (String) attributes.get("token");

        if (token != null && jwtService.validateToken(token)) {

            Long employeeId = jwtService.getEmployeeId(token); // ✅ ĐÚNG

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                    		employeeId.toString(), // Principal name = userId
                            null,
                            null
                    );

            return authentication;
        }

        return null;
    }
}
