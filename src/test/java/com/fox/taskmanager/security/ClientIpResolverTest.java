package com.fox.taskmanager.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class ClientIpResolverTest {

    private final ClientIpResolver resolver = new ClientIpResolver();

    @Test
    void resolvesFirstForwardedForValue() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.addHeader("X-Forwarded-For", "203.0.113.10, 192.168.50.199");
        request.setRemoteAddr("192.168.65.1");

        assertThat(resolver.resolve(request)).isEqualTo("203.0.113.10");
    }

    @Test
    void fallsBackToRealIpHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.addHeader("X-Real-IP", "203.0.113.20");
        request.setRemoteAddr("192.168.65.1");

        assertThat(resolver.resolve(request)).isEqualTo("203.0.113.20");
    }

    @Test
    void fallsBackToRemoteAddress() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setRemoteAddr("192.168.65.1");

        assertThat(resolver.resolve(request)).isEqualTo("192.168.65.1");
    }
}
