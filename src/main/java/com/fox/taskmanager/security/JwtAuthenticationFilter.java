package com.fox.taskmanager.security;

import com.fox.taskmanager.model.UserProfile;
import com.fox.taskmanager.repository.UserProfileRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final UserProfileRepository userProfileRepository;

    public JwtAuthenticationFilter(
            JwtTokenService jwtTokenService,
            UserProfileRepository userProfileRepository) {
        this.jwtTokenService = jwtTokenService;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String token = extractCookie(request, CookieService.ACCESS_TOKEN_COOKIE);

        if (token == null || !jwtTokenService.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String login = jwtTokenService.extractLogin(token);

        userProfileRepository.findByLogin(login)
                .filter(UserProfile::isEnabled)
                .filter(userProfile -> !userProfile.isAccountLocked())
                .ifPresent(this::authenticate);

        filterChain.doFilter(request, response);
    }

    private void authenticate(UserProfile userProfile) {
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + userProfile.getRole().name()));

        var authentication = new UsernamePasswordAuthenticationToken(
                userProfile.getLogin(), null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String extractCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
