package com.TP1.API.v1.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new io.swagger.v3.oas.models.servers.Server()
                                .url("http://localhost:8080")
                                .description("Local server"),
                        new io.swagger.v3.oas.models.servers.Server()
                                .url("http://127.0.0.1:8080")
                                .description("Local server")))
                .info(new Info()
                        .title("DevOps TP1 API Module")
                        .description("TP1 API")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Equipo N° ...")
                                .url("https://github.com/ulisespallares888/DevOps")
                        )
                );
    }
}

