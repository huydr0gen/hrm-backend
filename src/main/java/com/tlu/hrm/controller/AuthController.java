package com.tlu.hrm.controller;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tlu.hrm.dto.LoginRequest;
import com.tlu.hrm.dto.LoginResponse;
import com.tlu.hrm.entities.User;
import com.tlu.hrm.enums.UserStatus;
import com.tlu.hrm.repository.UserRepository;
import com.tlu.hrm.security.JwtProvider;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private final AuthenticationManager authenticationManager;
	
	private final JwtProvider jwtProvider;
	
	private final UserRepository userRepository;

	public AuthController(AuthenticationManager authenticationManager, JwtProvider jwtProvider,
			UserRepository userRepository) {
		super();
		this.authenticationManager = authenticationManager;
		this.jwtProvider = jwtProvider;
		this.userRepository = userRepository;
	}
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
		User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
		
		if (user.getStatus() == UserStatus.INACTIVE) {
			throw new DisabledException("Your account is inactive");
		}
		
		Authentication authentication;
		try {
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							request.getUsername(),
							request.getPassword()));
		} catch (BadCredentialsException e) {
			throw new BadCredentialsException("Invalid username or password");
		}
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String token = jwtProvider.generateToken(request.getUsername());
		
		LoginResponse response = new LoginResponse();
		response.setToken(token);
		response.setUsername(user.getUsername());
		response.setRoles(
				user.getRoles().stream()
				.map(r -> r.getName())
				.collect(Collectors.toSet()));
		response.setStatus(user.getStatus());
        response.setLastLogin(user.getLastLogin());
		
		return ResponseEntity.ok(response);
	}

}
