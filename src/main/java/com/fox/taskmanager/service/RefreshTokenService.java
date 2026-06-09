package com.fox.taskmanager.service;

import com.fox.taskmanager.model.AuthProvider;
import com.fox.taskmanager.model.RefreshToken;
import com.fox.taskmanager.model.UserProfile;

public interface RefreshTokenService {

    RefreshToken saveRefreshToken(
            String refreshToken,
            UserProfile userProfile,
            AuthProvider source,
            String deviceId,
            String userAgent,
            String ipAddress);

    RefreshToken getValidRefreshToken(String refreshToken);

    void revokeRefreshToken(String refreshToken);

    void revokeAllUserRefreshTokens(UserProfile userProfile);
}
