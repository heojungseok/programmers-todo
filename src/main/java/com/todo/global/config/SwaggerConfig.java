package com.todo.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("할 일 (Todo) API 명세서")
                        .description("Todo 서비스 API 명세서입니다.")
                        .version("v.1.0.0")
                );
    }
}
