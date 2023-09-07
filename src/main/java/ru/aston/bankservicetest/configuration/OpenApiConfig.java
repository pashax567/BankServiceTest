package ru.aston.bankservicetest.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bankServiceOpenApi() {
        final Info info = new Info()
                .title("Bank Service API")
                .version("1.0")
                ;
        return new OpenAPI().info(info);
    }
}
