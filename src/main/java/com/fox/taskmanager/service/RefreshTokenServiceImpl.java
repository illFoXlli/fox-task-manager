package com.fox.taskmanager.service;

import com.fox.taskmanager.config.JwtProperties;
import com.fox.taskmanager.model.AuthProvider;
import com.fox.taskmanager.model.RefreshToken;
import com.fox.taskmanager.model.UserProfile;
import com.fox.taskmanager.repository.RefreshTokenRepository;
import com.fox.taskmanager.security.TokenHashService;
import com.fox.taskmanager.support.AppTime;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenHashService tokenHashService;

    public RefreshTokenServiceImpl(
            JwtProperties jwtProperties,
            RefreshTokenRepository refreshTokenRepository,
            TokenHashService tokenHashService) {
        this.jwtProperties = jwtProperties;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenHashService = tokenHashService;
    }

    @Override
    @Transactional
    public RefreshToken saveRefreshToken(
            String refreshToken,
            UserProfile userProfile,
            String userAgent,
            String ipAddress) {
        RefreshToken token = new RefreshToken();

        token.setTokenHash(tokenHashService.hash(refreshToken));
        token.setUserProfile(userProfile);
        token.setSource(AuthProvider.WEB);
        token.setUserAgent(userAgent);
        token.setIpAddress(ipAddress);
        LocalDateTime now = AppTime.nowUtc();

        token.setIssuedAt(now);
        token.setExpiresAt(now.plusDays(jwtProperties.getRefreshExpirationDays()));

        return refreshTokenRepository.save(token);
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken getValidRefreshToken(String refreshToken) {
        String tokenHash = tokenHashService.hash(refreshToken);

        RefreshToken token = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token is not found"));

        if (token.isRevoked()) {
            throw new IllegalArgumentException("Refresh token is revoked");
        }

        if (token.isExpired()) {
            throw new IllegalArgumentException("Refresh token is expired");
        }

        return token;
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String refreshToken) {
        String tokenHash = tokenHashService.hash(refreshToken);

        refreshTokenRepository.findByTokenHash(tokenHash)
                .ifPresent(token -> {
                    token.setRevokedAt(AppTime.nowUtc());
                    refreshTokenRepository.save(token);
                });
    }

    @Override
    @Transactional
    public void revokeAllUserRefreshTokens(UserProfile userProfile) {
        refreshTokenRepository.findAllByUserProfile(userProfile)
                .forEach(token -> {
                    token.setRevokedAt(AppTime.nowUtc());
                    refreshTokenRepository.save(token);
                });
    }
}
