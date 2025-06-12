package com.travelservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth
                // Swagger 관련 경로 허용
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-resources/**", "/webjars/**", "/api-docs/**").permitAll()

                // 공개 API 허용 (회원가입, 로그인 등)
                .requestMatchers("/users/signup",  // 회원가입
                        "/users/login").permitAll()

                // 그 외는 인증 필요
                .anyRequest().authenticated());

        return http.build();
    }
}
