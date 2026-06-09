package com.fox.taskmanager.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class ClientIpResolver {

    private static final String FORWARDED_FOR_HEADER = "X-Forwarded-For";
    private static final String REAL_IP_HEADER = "X-Real-IP";

    public String resolve(HttpServletRequest request) {
        String forwardedFor = firstHeaderValue(request.getHeader(FORWARDED_FOR_HEADER));

        if (!forwardedFor.isBlank()) {
            return forwardedFor;
        }

        String realIp = firstHeaderValue(request.getHeader(REAL_IP_HEADER));

        if (!realIp.isBlank()) {
            return realIp;
        }

        return request.getRemoteAddr();
    }

    private String firstHeaderValue(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return value.split(",", 2)[0].trim();
    }
}
