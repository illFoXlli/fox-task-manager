package com.fox.taskmanager.security;

import com.fox.taskmanager.model.UserProfile;

public interface JwtTokenService {

    String createAccessToken(UserProfile userProfile);

    String createRefreshToken(UserProfile userProfile);

    String extractLogin(String token);

    boolean isTokenValid(String token);

    boolean isAccessTokenValid(String token);

    boolean isRefreshTokenValid(String token);
}
