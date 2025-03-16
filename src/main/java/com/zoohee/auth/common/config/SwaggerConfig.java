package com.zoohee.auth.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "Auth API", version = "1.0", description = "인증 관련 API 문서")
)
@Configuration
public class SwaggerConfig {
}
