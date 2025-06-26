package com.travelservice.domain.auth.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travelservice.domain.auth.dto.EmailSendRequest;
import com.travelservice.domain.auth.dto.EmailVerifyRequest;
import com.travelservice.domain.auth.service.EmailVerificationService;
import com.travelservice.domain.auth.service.PhoneVerificationService;
import com.travelservice.global.common.ApiResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "auth API - 본인 인증", description = "이메일 인증 기능을 제공.")
public class AuthVerificationController {
	private final EmailVerificationService emailVerificationService;
	private final PhoneVerificationService phoneVerificationService;

	@PostMapping("/email/send")
	public ResponseEntity<ApiResponse<Map<String, String>>> sendEmailVerificationCode(
		@RequestBody EmailSendRequest request) {
		emailVerificationService.sendVerificationEmail(request.getEmail());
		return ResponseEntity.ok(ApiResponse.ok(Map.of("message", "Verification email sent successfully")));
	}

	@PostMapping("/email/verify")
	public ResponseEntity<ApiResponse<Map<String, Boolean>>> verifyEmail(@RequestBody EmailVerifyRequest request) {
		boolean isVerified = emailVerificationService.verifyEmail(request.getEmail(), request.getCode());
		return ResponseEntity.ok(ApiResponse.ok(Map.of("verified", isVerified)));
	}

	// 전화번호 인증 관련 API - 조건 변경으로 휴대폰 인증 기능 주석처리(현재 사용하지 않음)
	/*
	@PostMapping("/phone/send")
	public ResponseEntity<ApiResponse<Map<String, String>>> sendPhoneVerificationCode(
		@RequestBody PhoneSendRequest request
	) {
		phoneVerificationService.sendVerificationCode(request.getPhoneNumber());
		return ResponseEntity.ok(
			ApiResponse.ok(Map.of("message", "Verification phoneNumber sent successfully"))
		);
	}

	@PostMapping("/phone/verify")
	public ResponseEntity<ApiResponse<Map<String, Boolean>>> verifyPhoneNumber(
		@RequestBody PhoneVerifyRequest request
	) {
		boolean isVerified = phoneVerificationService.verifyPhoneNumber(
			request.getPhoneNumber(),
			request.getCode()
		);
		return ResponseEntity.ok(ApiResponse.ok(Map.of("verified", isVerified)));
	}
	*/

}
