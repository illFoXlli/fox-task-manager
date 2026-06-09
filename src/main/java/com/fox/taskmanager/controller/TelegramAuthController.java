package com.fox.taskmanager.controller;

import com.fox.taskmanager.config.AppConstants;
import com.fox.taskmanager.config.WebRedirect;
import com.fox.taskmanager.dto.auth.AuthResponse;
import com.fox.taskmanager.dto.auth.TelegramCompleteRequest;
import com.fox.taskmanager.dto.auth.TelegramSessionStatusResponse;
import com.fox.taskmanager.dto.auth.TelegramStartSession;
import com.fox.taskmanager.exception.AuthException;
import com.fox.taskmanager.service.TelegramAuthSessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TelegramAuthController {

    private static final String LOGIN_MODE = "login";
    private static final String REGISTER_MODE = "register";

    private final TelegramAuthSessionService telegramAuthSessionService;

    public TelegramAuthController(TelegramAuthSessionService telegramAuthSessionService) {
        this.telegramAuthSessionService = telegramAuthSessionService;
    }

    @GetMapping("/auth/telegram/start")
    public void telegramStart(
            @RequestParam(value = "mode", required = false) String mode,
            @RequestParam(value = "origin", required = false) String origin,
            HttpServletRequest request,
            HttpServletResponse response) {
        String resolvedMode = resolveMode(mode);
        TelegramStartSession session = telegramAuthSessionService.createSession(
                resolvedMode,
                resolveBaseUrl(request, origin));

        WebRedirect.sendRelativeRedirect(response, session.getTelegramUrl());
    }

    @GetMapping("/api/auth/telegram/status")
    public ResponseEntity<TelegramSessionStatusResponse> telegramStatus(
            @RequestParam("token") String token) {
        return ResponseEntity.ok(telegramAuthSessionService.getStatus(token));
    }

    @PostMapping("/api/auth/telegram/complete")
    public ResponseEntity<AuthResponse> telegramComplete(
            @Valid @RequestBody TelegramCompleteRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        return ResponseEntity.ok(telegramAuthSessionService.complete(
                request.getToken(),
                httpRequest,
                httpResponse));
    }

    @GetMapping("/auth/telegram/return")
    public void telegramReturn(
            @RequestParam("token") String token,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            AuthResponse authResponse = telegramAuthSessionService.complete(
                    token,
                    request,
                    response);
            WebRedirect.sendRelativeRedirect(response, authResponse.getRedirectUrl());
        } catch (AuthException exception) {
            WebRedirect.sendRelativeRedirect(
                    response,
                    AppConstants.Route.LOGIN + "?telegramError=true");
        }
    }

    private String resolveMode(String mode) {
        if (REGISTER_MODE.equals(mode)) {
            return REGISTER_MODE;
        }

        return LOGIN_MODE;
    }

    private String resolveBaseUrl(HttpServletRequest request, String origin) {
        String explicitOrigin = resolveExplicitOrigin(request, origin);

        if (explicitOrigin != null) {
            return explicitOrigin;
        }

        String refererOrigin = resolveExplicitOrigin(request, request.getHeader("Referer"));

        if (refererOrigin != null) {
            return refererOrigin;
        }

        String scheme = firstHeaderValue(request.getHeader("X-Forwarded-Proto"));
        String host = firstHeaderValue(request.getHeader("X-Forwarded-Host"));

        if (scheme == null) {
            scheme = request.getScheme();
        }

        if (host == null) {
            host = request.getHeader("Host");
        }

        if (host == null || host.isBlank()) {
            host = request.getServerName();
        }

        String forwardedPort = firstHeaderValue(request.getHeader("X-Forwarded-Port"));

        if (shouldAppendPort(host, scheme, forwardedPort)) {
            host = host + ":" + forwardedPort;
        }

        return scheme + "://" + host;
    }

    private String resolveExplicitOrigin(HttpServletRequest request, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            URI uri = new URI(value.trim());

            if (uri.getScheme() == null || uri.getHost() == null) {
                return null;
            }

            if (!"https".equals(uri.getScheme()) && !"http".equals(uri.getScheme())) {
                return null;
            }

            if (!isAllowedOriginHost(request, uri.getHost())) {
                return null;
            }

            String port = uri.getPort() > 0 ? ":" + uri.getPort() : "";

            return uri.getScheme() + "://" + uri.getHost() + port;
        } catch (URISyntaxException exception) {
            return null;
        }
    }

    private boolean isAllowedOriginHost(HttpServletRequest request, String originHost) {
        String forwardedHost = firstHeaderValue(request.getHeader("X-Forwarded-Host"));
        String requestHost = request.getHeader("Host");
        String serverName = request.getServerName();

        return isSameHost(originHost, forwardedHost)
                || isSameHost(originHost, requestHost)
                || isSameHost(originHost, serverName);
    }

    private boolean isSameHost(String originHost, String candidateHost) {
        if (originHost == null || candidateHost == null || candidateHost.isBlank()) {
            return false;
        }

        return originHost.equalsIgnoreCase(stripPort(candidateHost));
    }

    private String stripPort(String host) {
        int portSeparatorIndex = host.indexOf(":");

        if (portSeparatorIndex < 0) {
            return host;
        }

        return host.substring(0, portSeparatorIndex);
    }

    private boolean shouldAppendPort(String host, String scheme, String port) {
        if (port == null || port.isBlank() || host.contains(":")) {
            return false;
        }

        return !("https".equals(scheme) && "443".equals(port))
                && !("http".equals(scheme) && "80".equals(port));
    }

    private String firstHeaderValue(String headerValue) {
        if (headerValue == null || headerValue.isBlank()) {
            return null;
        }

        return headerValue.split(",")[0].trim();
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<AuthResponse> handleAuthException(AuthException exception) {
        return ResponseEntity.badRequest()
                .body(new AuthResponse(exception.getMessage(), null));
    }

}
