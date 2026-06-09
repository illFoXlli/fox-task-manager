package com.fox.taskmanager.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;

class WebRedirectTest {

    @Test
    void sendsRelativeRedirectWithoutRebuildingHostOrProtocol() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        WebRedirect.sendRelativeRedirect(response, AppConstants.Route.LOGIN);

        assertThat(response.getStatus()).isEqualTo(302);
        assertThat(response.getHeader(HttpHeaders.LOCATION))
                .isEqualTo(AppConstants.Route.LOGIN);
    }
}
