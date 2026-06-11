package com.fox.taskmanager.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String COOKIE_AUTH_SCHEME = "cookieAuth";

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Your Notes API")
                        .description("REST API for authentication and personal notes.")
                        .version("0.1.0"))
                .servers(List.of(new Server()
                        .url("/")
                        .description("Current host")))
                .components(new Components()
                        .addSecuritySchemes(
                                COOKIE_AUTH_SCHEME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.COOKIE)
                                        .name(AppConstants.Cookie.ACCESS_TOKEN_NAME)));
    }
}
