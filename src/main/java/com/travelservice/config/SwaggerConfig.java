package com.travelservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI openApi() {
		return new OpenAPI()
			.addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
			.components(new Components().addSecuritySchemes("Bearer Authentication",
				new SecurityScheme()
					.name("Authorization")
					.type(SecurityScheme.Type.HTTP)
					.scheme("bearer")
					.bearerFormat("JMWT")))
			.info(new Info()
				.title("API 문서 제목")
				.version("v1.0")
				.description("API 설명"));
	}
}
