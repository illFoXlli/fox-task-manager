package com.fox.taskmanager.controller;

import com.fox.taskmanager.config.AppConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelControllerAdvice {

    private static final String HOME_HOST = "home.fox.kh.ua";

    @ModelAttribute("assetVersion")
    public String assetVersion() {
        return AppConstants.Asset.VERSION;
    }

    @ModelAttribute("homeUrl")
    public String homeUrl(HttpServletRequest request) {
        String scheme = firstHeaderValue(request.getHeader("X-Forwarded-Proto"));
        String forwardedPort = firstHeaderValue(request.getHeader("X-Forwarded-Port"));
        String host = firstNonBlank(
                firstHeaderValue(request.getHeader("X-Forwarded-Host")),
                firstHeaderValue(request.getHeader("Host")));

        if (scheme == null) {
            scheme = request.getScheme();
        }

        String port = resolvePort(host, forwardedPort, request.getServerPort(), scheme);

        return scheme + "://" + HOME_HOST + port;
    }

    private String resolvePort(
            String host,
            String forwardedPort,
            int serverPort,
            String scheme) {
        String hostPort = extractPort(host);

        if (hostPort != null) {
            return ":" + hostPort;
        }

        if (isExplicitPort(forwardedPort, scheme)) {
            return ":" + forwardedPort;
        }

        if (isExplicitPort(String.valueOf(serverPort), scheme)) {
            return ":" + serverPort;
        }

        return "";
    }

    private String extractPort(String host) {
        if (host == null || host.isBlank()) {
            return null;
        }

        int separatorIndex = host.lastIndexOf(":");

        if (separatorIndex < 0 || separatorIndex == host.length() - 1) {
            return null;
        }

        return host.substring(separatorIndex + 1);
    }

    private boolean isExplicitPort(String port, String scheme) {
        if (port == null || port.isBlank()) {
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

    private String firstNonBlank(String firstValue, String secondValue) {
        if (firstValue != null && !firstValue.isBlank()) {
            return firstValue;
        }

        return secondValue;
    }
}
