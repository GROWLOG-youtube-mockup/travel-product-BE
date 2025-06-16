package com.travelservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/h2-console/**",
					"/swagger-ui/**",
					"/v3/api-docs/**"
				).permitAll()
				.anyRequest().permitAll() // 개발 중 전체 허용
			)
			.csrf(csrf -> csrf
				.ignoringRequestMatchers(
					"/h2-console/**",
					"/swagger-ui/**",
					"/v3/api-docs/**"
				)
			)
			.headers(headers -> headers
				.frameOptions()
				.disable() // H2 콘솔 iframe 허용
			);

		return http.build();
	}
}
