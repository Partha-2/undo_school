package com.undoschool.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Undo School API")
                        .description("Global Class Offering Booking System — teachers create offerings, parents browse and book.")
                        .version("1.0.0")
                        .license(new License().name("MIT")));
    }
}
