package com.tlu.hrm.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tlu.hrm.entities.User;
import com.tlu.hrm.repository.UserRepository;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

	private final UserRepository userRepository;

    @Value("${jwt.refresh-expiration-minutes:30}")
    private int refreshTokenExpiryMinutes;

    public RefreshTokenServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String createRefreshToken(User user) {
        String token = UUID.randomUUID().toString();

        user.setRefreshToken(token);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusMinutes(refreshTokenExpiryMinutes));

        userRepository.save(user);

        return token;
    }

    @Override
    public boolean isRefreshTokenExpired(User user) {
        return user.getRefreshTokenExpiry() == null ||
               user.getRefreshTokenExpiry().isBefore(LocalDateTime.now());
    }

    @Override
    public void revokeRefreshToken(User user) {
        user.setRefreshToken(null);
        user.setRefreshTokenExpiry(null);
        userRepository.save(user);
    }
}
