package com.travelservice.domain.auth.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travelservice.domain.auth.dto.EmailSendRequest;
import com.travelservice.domain.auth.dto.EmailVerifyRequest;
import com.travelservice.domain.auth.dto.PhoneSendRequest;
import com.travelservice.domain.auth.dto.PhoneVerifyRequest;
import com.travelservice.domain.auth.service.EmailVerificationService;
import com.travelservice.domain.auth.service.PhoneVerificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthVerificationController {
	private final EmailVerificationService emailVerificationService;
	private final PhoneVerificationService phoneVerificationService;

	@PostMapping("/email/send")
	public ResponseEntity<Map<String, String>> sendEmailVerificationCode(@RequestBody EmailSendRequest request) {
		emailVerificationService.sendVerificationEmail(request.getEmail());
		return ResponseEntity.ok(Map.of("message", "Verification email sent successfully"));
	}

	@PostMapping("/email/verify")
	public ResponseEntity<Map<String, Boolean>> verifyEmail(@RequestBody EmailVerifyRequest request) {
		boolean isVerified = emailVerificationService.verifyEmail(request.getEmail(), request.getCode());
		return ResponseEntity.ok(Map.of("verified", isVerified));
	}

	@PostMapping("/phone/send")
	public ResponseEntity<Map<String, String>> sendPhoneVerificationCode(@RequestBody PhoneSendRequest request) {
		phoneVerificationService.sendVerificationCode(request.getPhoneNumber());
		return ResponseEntity.ok(Map.of("message", "Verification phoneNumber sent successfully"));
	}

	@PostMapping("/phone/verify")
	public ResponseEntity<Map<String, Boolean>> verifyPhoneNumber(@RequestBody PhoneVerifyRequest request) {
		boolean isVerified = phoneVerificationService.verifyPhoneNumber(request.getPhoneNumber(), request.getCode());
		return ResponseEntity.ok(Map.of("verified", isVerified));
	}
}
