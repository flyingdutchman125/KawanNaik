package com.kawannaik.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI kawanNaikOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("KawanNaik API")
                        .description("Hiking Buddy Finder — REST API documentation.\n\n"
                                + "KawanNaik membantu para pendaki menemukan teman pendakian, "
                                + "menjelajahi gunung dan jalur, serta mengelola rencana pendakian bersama.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Tim KawanNaik")
                                .email("admin@kawannaik.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Masukkan JWT token yang didapat dari endpoint login.\n"
                                                + "Format: Bearer {token}")));
    }
}
