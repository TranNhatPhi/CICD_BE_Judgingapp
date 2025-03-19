package com.project.judging.Config.Security;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:9000"); // cho nay de lam j ong
        server.setDescription("Development");

        Info information = new Info()
                .title("CSIT321 Judging API")
                .version("1.0")
                .description("This API provides endpoints for managing judging processes in CSIT321.");

        return new OpenAPI().info(information).servers(List.of(server));
    }
}
