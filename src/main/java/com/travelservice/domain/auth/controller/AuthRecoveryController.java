package com.travelservice.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travelservice.domain.auth.dto.FindEmailRequestDto;
import com.travelservice.domain.auth.dto.ResetPasswordRequestDto;
import com.travelservice.domain.auth.service.AuthRecoveryService;
import com.travelservice.global.common.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthRecoveryController {
	private final AuthRecoveryService authRecoveryService;

	@PostMapping("/find-email")
	public ResponseEntity<ApiResponse<String>> findEmail(@RequestBody FindEmailRequestDto requestDto) {
		String email = authRecoveryService.findEmail(requestDto);
		log.info("[GrowLog - 여행서비스] 이메일 찾기 요청: {}", requestDto);
		return ResponseEntity.ok(ApiResponse.ok(email));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPasswordRequestDto requestDto) {
		authRecoveryService.resetPassword(requestDto);
		log.info("[GrowLog - 여행서비스] 비밀번호 재설정 요청: {}", requestDto);
		return ResponseEntity.ok(ApiResponse.ok(null));
	}
}
