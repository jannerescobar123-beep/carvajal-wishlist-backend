package com.carvajal.wishlist.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI wishlistOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Carvajal Wishlist API")
                        .version("1.0.0")
                        .description(
                                "API REST para la gestión de productos y wishlist. " +
                                "Utiliza autenticación JWT Bearer. " +
                                "Para obtener un token: POST /api/auth/login con credenciales de usuario."
                        ))
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList("bearerAuth")
                )
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description(
                                                        "JWT Bearer token authentication. " +
                                                        "Obtén el token con POST /api/auth/login " +
                                                        "y úsalo en el header: Authorization: Bearer {token}"
                                                )
                                )
                );
    }
}