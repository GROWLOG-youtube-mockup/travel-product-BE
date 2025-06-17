package com.travelservice.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travelservice.domain.auth.dto.LoginRequestDto;
import com.travelservice.domain.auth.dto.LoginResponseDto;
import com.travelservice.domain.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	//로그인
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {
		LoginResponseDto response = authService.login(requestDto);
		return ResponseEntity.ok(response);
	}
}
