package com.travelservice.domain.auth.service;

import org.springframework.stereotype.Service;

import com.travelservice.domain.auth.entity.EmailVerification;
import com.travelservice.domain.auth.repository.EmailVerificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

	private final EmailVerificationRepository emailVerificationRepository;

	public void sendVerificationEmail(String email) {
		String verificationCode = generateVerificationCode();

		EmailVerification verification = EmailVerification.builder()
			.email(email)
			.code(verificationCode)
			.verified(false)
			.build();

		emailVerificationRepository.save(verification);
		
		log.info("Verification email sent to {} with code: {}", email, verificationCode);
	}

	private String generateVerificationCode() {
		return String.valueOf((int)(Math.random() * 900000) + 1000); //6자리 랜덤 숫자 생성
	}
}
