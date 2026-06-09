package com.fox.taskmanager.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;

public final class WebRedirect {

    private WebRedirect() {
    }

    public static void sendRelativeRedirect(HttpServletResponse response, String location) {
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader(HttpHeaders.LOCATION, location);
    }
}
