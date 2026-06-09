package com.fox.taskmanager.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.fox.taskmanager.config.AppConstants;
import com.fox.taskmanager.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class AuthPageControllerTest {

    @Test
    void logoutPageClearsAuthAndRedirectsToLogin() {
        AuthService authService = mock(AuthService.class);
        AuthPageController controller = new AuthPageController(authService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.logoutPage(request, response);

        verify(authService).logout(request, response);
        assertThat(response.getStatus()).isEqualTo(302);
        assertThat(response.getHeader(HttpHeaders.LOCATION))
                .isEqualTo(AppConstants.Route.LOGIN);
    }
}
