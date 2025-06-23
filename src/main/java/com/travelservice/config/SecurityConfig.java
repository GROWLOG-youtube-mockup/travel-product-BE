package com.travelservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.travelservice.global.common.jwt.JwtAuthenticationFilter;
import com.travelservice.global.common.jwt.JwtTokenProvider;

@Configuration
public class SecurityConfig {
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Swagger 관련 경로를 정리한 화이트리스트
	private static final String[] SWAGGER_WHITELIST = {
		"/swagger-ui.html",
		"/swagger-ui/**",
		"/v3/api-docs/**",
		"/api-docs/**",  // application.yml에서 path: /api-docs 로 설정했기 때문에 필요
		"/swagger-resources/**",
		"/webjars/**"
	};

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenProvider jwtTokenProvider) throws
		Exception {
		http
			.csrf(csrf -> csrf.disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(SWAGGER_WHITELIST).permitAll()
				.requestMatchers("/products", "/products/**", "/images/**").permitAll()
				.requestMatchers("/users/signup", "/users/login").permitAll()
				.requestMatchers("/auth/**").permitAll() // 이메일 인증 등 허용
				.requestMatchers("/admin/**").hasAnyAuthority("ADMIN", "SUPER_ADMIN")
				.anyRequest().authenticated() //  나머지는 인증 필요
			)
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
			.formLogin(form -> form.disable())
			.httpBasic(basic -> basic.disable());

		return http.build();
	}
}
