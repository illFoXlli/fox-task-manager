package com.fox.taskmanager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fox.taskmanager.config.JwtProperties;
import com.fox.taskmanager.model.RefreshToken;
import com.fox.taskmanager.model.UserProfile;
import com.fox.taskmanager.repository.RefreshTokenRepository;
import com.fox.taskmanager.security.TokenHashService;
import com.fox.taskmanager.support.AppTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    private static final String RAW_TOKEN = "refresh-token";
    private static final String TOKEN_HASH = "hashed-refresh-token";

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private TokenHashService tokenHashService;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    @Test
    void getValidRefreshTokenRejectsRevokedToken() {
        RefreshToken token = new RefreshToken();

        token.setRevokedAt(AppTime.nowUtc());
        token.setExpiresAt(AppTime.nowUtc().plusDays(1));

        when(tokenHashService.hash(RAW_TOKEN)).thenReturn(TOKEN_HASH);
        when(refreshTokenRepository.findByTokenHash(TOKEN_HASH))
                .thenReturn(Optional.of(token));

        assertThatThrownBy(() -> refreshTokenService.getValidRefreshToken(RAW_TOKEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Refresh token is revoked");
    }

    @Test
    void getValidRefreshTokenRejectsExpiredToken() {
        RefreshToken token = new RefreshToken();

        token.setExpiresAt(AppTime.nowUtc().minusDays(1));

        when(tokenHashService.hash(RAW_TOKEN)).thenReturn(TOKEN_HASH);
        when(refreshTokenRepository.findByTokenHash(TOKEN_HASH))
                .thenReturn(Optional.of(token));

        assertThatThrownBy(() -> refreshTokenService.getValidRefreshToken(RAW_TOKEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Refresh token is expired");
    }

    @Test
    void revokeAllUserRefreshTokensMarksTokensRevoked() {
        UserProfile userProfile = new UserProfile();
        RefreshToken firstToken = new RefreshToken();
        RefreshToken secondToken = new RefreshToken();

        when(refreshTokenRepository.findAllByUserProfile(userProfile))
                .thenReturn(List.of(firstToken, secondToken));

        refreshTokenService.revokeAllUserRefreshTokens(userProfile);

        assertThat(firstToken.getRevokedAt()).isNotNull();
        assertThat(secondToken.getRevokedAt()).isNotNull();
        verify(refreshTokenRepository).save(firstToken);
        verify(refreshTokenRepository).save(secondToken);
    }
}
