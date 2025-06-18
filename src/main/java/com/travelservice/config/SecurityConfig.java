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
