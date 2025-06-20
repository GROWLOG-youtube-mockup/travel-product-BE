package com.travelservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	// Swagger 관련 경로를 정리한 화이트리스트
	private static final String[] SWAGGER_WHITELIST = {
		"/swagger-ui.html",
		"/swagger-ui/**",
		"/v3/api-docs/**",
		"/api-docs",  // application.yml에서 path: /api-docs 로 설정했기 때문에 필요
		"/swagger-resources/**",
		"/webjars/**"
	};

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf
				.ignoringRequestMatchers(
					"/h2-console/**",
					"/swagger-ui/**",
					"/v3/api-docs/**"
				)
				.disable()
			)
			.headers(headers -> headers.frameOptions().disable())
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/h2-console/**",
					"/swagger-ui/**",
					"/v3/api-docs/**"
				).permitAll()
				.anyRequest().permitAll() // 반드시 마지막에 선언
			);
		return http.build();
	}
}
