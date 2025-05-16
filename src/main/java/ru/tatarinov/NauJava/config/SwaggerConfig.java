package ru.tatarinov.NauJava.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI libraryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Library Management API")
                        .description("API для управления библиотекой")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Developer")
                                .email("dev@example.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("Документация")
                        .url("https://example.com/docs"));
    }
}
