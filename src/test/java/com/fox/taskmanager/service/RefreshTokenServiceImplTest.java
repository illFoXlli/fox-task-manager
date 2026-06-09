package com.fox.taskmanager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fox.taskmanager.config.JwtProperties;
import com.fox.taskmanager.model.AuthProvider;
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
    private static final String DEVICE_ID = "device-id";
    private static final String USER_AGENT = "Mozilla";
    private static final String IP_ADDRESS = "192.168.65.1";

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private TokenHashService tokenHashService;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    @Test
    void saveRefreshTokenReusesCurrentDeviceTokenAndDeletesDuplicates() {
        final UserProfile userProfile = new UserProfile();
        RefreshToken currentToken = new RefreshToken();
        final RefreshToken duplicateToken = new RefreshToken();

        currentToken.setRevokedAt(AppTime.nowUtc());

        when(tokenHashService.hash(RAW_TOKEN)).thenReturn(TOKEN_HASH);
        when(jwtProperties.getRefreshExpirationDays()).thenReturn(30L);
        when(refreshTokenRepository.findCurrentDeviceTokens(
                userProfile,
                AuthProvider.WEB,
                DEVICE_ID))
                .thenReturn(List.of(currentToken, duplicateToken));
        when(refreshTokenRepository.save(currentToken)).thenReturn(currentToken);

        RefreshToken savedToken = refreshTokenService.saveRefreshToken(
                RAW_TOKEN,
                userProfile,
                AuthProvider.WEB,
                DEVICE_ID,
                USER_AGENT,
                IP_ADDRESS);

        assertThat(savedToken).isSameAs(currentToken);
        assertThat(savedToken.getTokenHash()).isEqualTo(TOKEN_HASH);
        assertThat(savedToken.getSource()).isEqualTo(AuthProvider.WEB);
        assertThat(savedToken.getDeviceId()).isEqualTo(DEVICE_ID);
        assertThat(savedToken.getIpAddress()).isEqualTo(IP_ADDRESS);
        assertThat(savedToken.getRevokedAt()).isNull();
        assertThat(savedToken.getIssuedAt()).isNotNull();
        assertThat(savedToken.getExpiresAt()).isEqualTo(savedToken.getIssuedAt().plusDays(30));
        verify(refreshTokenRepository).delete(duplicateToken);
        verify(refreshTokenRepository).save(currentToken);
    }

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
    void getValidRefreshTokenMarksTokenAsUsed() {
        RefreshToken token = new RefreshToken();

        token.setExpiresAt(AppTime.nowUtc().plusDays(1));

        when(tokenHashService.hash(RAW_TOKEN)).thenReturn(TOKEN_HASH);
        when(refreshTokenRepository.findByTokenHash(TOKEN_HASH))
                .thenReturn(Optional.of(token));
        when(refreshTokenRepository.save(token)).thenReturn(token);

        RefreshToken validToken = refreshTokenService.getValidRefreshToken(RAW_TOKEN);

        assertThat(validToken.getLastUsedAt()).isNotNull();
        verify(refreshTokenRepository).save(token);
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
