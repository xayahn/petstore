package com.petstore.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * OpenAPI/Swagger configuration for API documentation.
 * 
 * Provides:
 * - API metadata (title, version, description)
 * - Contact and license information
 * - Server URLs for different environments
 * - Available at /api/docs and /swagger-ui/
 * 
 * @since 1.0.0
 */
@Configuration
public class SpringDocOpenApiConfig {

    /**
     * Configure OpenAPI documentation.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Petstore API")
                        .version("1.0.0")
                        .description("A peer-to-peer marketplace for buying and selling pets. " +
                                "Features include user authentication, pet catalog browsing, shopping cart, " +
                                "seller account management, and order processing.")
                        .contact(new Contact()
                                .name("Petstore Team")
                                .url("https://github.com/petstore")
                                .email("support@petstore.example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(Arrays.asList(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server"),
                        new Server()
                                .url("http://localhost:5173")
                                .description("Frontend Dev Server"),
                        new Server()
                                .url("https://api.petstore.example.com")
                                .description("Production Server")));
    }
}
