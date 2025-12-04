package com.tlu.hrm.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashTest {

	public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "1";  // mật khẩu mặc định của bạn
        String hashed = encoder.encode(rawPassword);

        System.out.println("BCrypt hash: " + hashed);
    }
}
