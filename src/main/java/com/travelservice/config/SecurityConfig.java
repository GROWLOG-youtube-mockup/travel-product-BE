package com.travelservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

	private static final String[] SWAGGER_WHITELIST = {
		"/swagger-ui.html",
		"/swagger-ui/**",
		"/v3/api-docs/**",
		"/api-docs/**",
		"/swagger-resources/**",
		"/webjars/**"
	};

	private static final String[] DEV_WHITELIST = {
		"/h2-console/**"
	};

	@Bean
	public SecurityFilterChain filterChain(
		HttpSecurity http, JwtTokenProvider jwtTokenProvider, UserRepository userRepository) throws Exception {
		http
			.csrf(csrf -> csrf
				.ignoringRequestMatchers(SWAGGER_WHITELIST)
				.ignoringRequestMatchers(DEV_WHITELIST)
				.disable()
			)
			.headers(headers -> headers.frameOptions().disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(SWAGGER_WHITELIST).permitAll()
				.requestMatchers(DEV_WHITELIST).permitAll()
				.requestMatchers("/users/signup", "/users/login").permitAll()
				.requestMatchers("/auth/**").permitAll()
				.anyRequest().authenticated()
			)
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, userRepository),
				UsernamePasswordAuthenticationFilter.class)
			.formLogin(form -> form.disable())
			.httpBasic(basic -> basic.disable());

		return http.build();
	}
}
