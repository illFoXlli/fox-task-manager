package com.fox.taskmanager.security;

import com.fox.taskmanager.config.JwtProperties;
import com.fox.taskmanager.model.UserProfile;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    private static final String TOKEN_TYPE = "type";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtTokenServiceImpl(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String createAccessToken(UserProfile userProfile) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(
                jwtProperties.getAccessExpirationMinutes() * 60);

        return createToken(userProfile, ACCESS_TOKEN_TYPE, now, expiresAt);
    }

    @Override
    public String createRefreshToken(UserProfile userProfile) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(
                jwtProperties.getRefreshExpirationDays() * 24 * 60 * 60);

        return createToken(userProfile, REFRESH_TOKEN_TYPE, now, expiresAt);
    }

    @Override
    public String extractLogin(String token) {
        return extractClaims(token).getSubject();
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    private String createToken(
            UserProfile userProfile,
            String tokenType,
            Instant issuedAt,
            Instant expiresAt) {
        return Jwts.builder()
                .subject(userProfile.getLogin())
                .claim("userId", userProfile.getId())
                .claim("role", userProfile.getRole().name())
                .claim(TOKEN_TYPE, tokenType)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
