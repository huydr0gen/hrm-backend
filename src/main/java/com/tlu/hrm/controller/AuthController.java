package com.tlu.hrm.controller;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tlu.hrm.dto.LoginRequest;
import com.tlu.hrm.dto.LoginResponse;
import com.tlu.hrm.dto.RefreshTokenRequest;
import com.tlu.hrm.entities.User;
import com.tlu.hrm.enums.UserStatus;
import com.tlu.hrm.repository.UserRepository;
import com.tlu.hrm.security.JwtProvider;
import com.tlu.hrm.service.AuditLogService;
import com.tlu.hrm.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;

@Tag(
	    name = "Authentication",
	    description = "API xác thực người dùng: đăng nhập, refresh token"
	)
@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private final AuthenticationManager authenticationManager;
	
    private final JwtProvider jwtProvider;
    
    private final UserRepository userRepository;
    
    private final UserService userService;
    
    private final AuditLogService auditLogService;

	public AuthController(AuthenticationManager authenticationManager, JwtProvider jwtProvider,
			UserRepository userRepository, UserService userService, AuditLogService auditLogService) {
		super();
		this.authenticationManager = authenticationManager;
		this.jwtProvider = jwtProvider;
		this.userRepository = userRepository;
		this.userService = userService;
		this.auditLogService = auditLogService;
	}

	// LOGIN -----------------------------------------------------------------------
	@Operation(
	        summary = "Đăng nhập hệ thống",
	        description = """
	            Màn hình: Login
	            
	            Luồng nghiệp vụ:
	            - Người dùng nhập username + password
	            - Nếu hợp lệ → trả về accessToken + refreshToken
	            - Token dùng cho các API bảo mật phía sau
	            
	            Ghi chú cho FE:
	            - accessToken dùng để gắn vào Authorization header
	            - refreshToken dùng khi accessToken hết hạn
	            """
	        )
	@ApiResponses({
	        @ApiResponse(responseCode = "200", description = "Đăng nhập thành công"),
	        @ApiResponse(responseCode = "400", description = "Sai username hoặc password"),
	        @ApiResponse(responseCode = "403", description = "Tài khoản bị khóa / inactive")
	    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (user.getStatus() == UserStatus.INACTIVE)
            throw new DisabledException("Your account is inactive");

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {

            // ⭐ GHI LOG khi login failed
            auditLogService.log(null, "LOGIN_FAILED", "Username: " + request.getUsername());

            throw new BadCredentialsException("Invalid username or password");
        }

        String accessToken = jwtProvider.generateAccessToken(user.getUsername());
        String refreshToken = jwtProvider.generateRefreshToken(user.getUsername());

        userService.updateRefreshToken(user.getId(), refreshToken);

        user.setLastLogin(java.time.LocalDateTime.now());
        userRepository.save(user);

        // ⭐ GHI LOG login thành công
        auditLogService.log(user.getId(), "LOGIN_SUCCESS", "User logged in successfully");

        return ResponseEntity.ok(buildResponse(user, accessToken, refreshToken));
    }

    // REFRESH TOKEN -------------------------------------------------------------------
	@Operation(
	        summary = "Làm mới access token",
	        description = """
	            Màn hình: Token refresh (ẩn)
	            
	            Khi nào gọi:
	            - accessToken hết hạn (401)
	            
	            Luồng:
	            - FE gửi refreshToken
	            - Backend kiểm tra hợp lệ
	            - Trả về accessToken mới + refreshToken mới
	            
	            Ghi chú:
	            - refreshToken sẽ bị rotate (đổi mới)
	            - FE phải lưu lại token mới
	            """
	        )
	@ApiResponses({
	        @ApiResponse(responseCode = "200", description = "Refresh token thành công"),
	        @ApiResponse(responseCode = "400", description = "Refresh token không hợp lệ hoặc hết hạn")
	        })
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshTokenRequest request) {

        User user = userRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (!jwtProvider.validateToken(request.getRefreshToken())) {

            // ⭐ Log refresh token bị từ chối
            auditLogService.log(user.getId(), "REFRESH_TOKEN_FAILED", "Expired or invalid refresh token");

            throw new RuntimeException("Refresh token expired or invalid");
        }

        String newAccessToken = jwtProvider.generateAccessToken(user.getUsername());
        String newRefreshToken = jwtProvider.generateRefreshToken(user.getUsername());

        userService.updateRefreshToken(user.getId(), newRefreshToken);

        // ⭐ Log refresh token thành công
        auditLogService.log(user.getId(), "REFRESH_TOKEN_SUCCESS", "Refresh token rotated successfully");

        return ResponseEntity.ok(buildResponse(user, newAccessToken, newRefreshToken));
    }

    // BUILD RESPONSE -----------------------------------------------------------------
    private LoginResponse buildResponse(User user, String accessToken, String refreshToken) {
        LoginResponse res = new LoginResponse();
        res.setAccessToken(accessToken);
        res.setRefreshToken(refreshToken);
        res.setUsername(user.getUsername());
        res.setRoles(user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()));
        res.setStatus(user.getStatus());
        res.setLastLogin(user.getLastLogin());
        return res;
    }

}
