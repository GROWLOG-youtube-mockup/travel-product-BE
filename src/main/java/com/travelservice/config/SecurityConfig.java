package com.travelservice.config;

import static org.springframework.security.config.Customizer.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.travelservice.domain.user.repository.UserRepository;
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

	private static final String[] DEV_WHITELIST = {
		"/h2-console/**"
	};

	@Bean
	public SecurityFilterChain securityFilterChain(
		HttpSecurity http, JwtTokenProvider jwtTokenProvider, UserRepository userRepository
	) throws Exception {
		http
			.cors(withDefaults())
			.csrf(csrf -> csrf.disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.headers(headers -> headers.frameOptions().sameOrigin())
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(SWAGGER_WHITELIST).permitAll()
				.requestMatchers(DEV_WHITELIST).permitAll()
				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.requestMatchers("/", "/error").permitAll()
				.requestMatchers("/users/signup").permitAll()
				.requestMatchers("/products", "/products/**", "/images/**").permitAll()
				.requestMatchers("/regions").permitAll()
				.requestMatchers("/users/signup", "/users/login").permitAll()
				.requestMatchers("/auth/**").permitAll() // 이메일 인증 등 허용
				.requestMatchers("/admin/**").hasAnyAuthority("ADMIN", "SUPER_ADMIN")
				.anyRequest().authenticated() //  나머지는 인증 필요
			)
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, userRepository),
				UsernamePasswordAuthenticationFilter.class)
			.formLogin(form -> form.disable())
			.httpBasic(basic -> basic.disable());

		return http.build();
	}
}
