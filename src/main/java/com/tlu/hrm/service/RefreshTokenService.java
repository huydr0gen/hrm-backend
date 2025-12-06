package com.tlu.hrm.service;

import com.tlu.hrm.entities.User;

public interface RefreshTokenService {

	String createRefreshToken(User user);

    boolean isRefreshTokenExpired(User user);

    void revokeRefreshToken(User user);
}
