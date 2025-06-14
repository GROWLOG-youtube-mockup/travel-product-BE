package com.travelservice.domain.auth.service;

import org.springframework.stereotype.Service;

import com.travelservice.domain.auth.entity.PhoneVerification;
import com.travelservice.domain.auth.repository.PhoneVerificationRepository;
import com.travelservice.global.common.exception.CustomException;
import com.travelservice.global.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhoneVerificationService {
	private final PhoneVerificationRepository phoneVerificationRepository;

	// 전화번호 인증 코드 전송
	public void sendVerificationCode(String phoneNumber) {
		String code = generateVerificationCode();

		PhoneVerification phoneVerification = PhoneVerification.builder()
			.phoneNumber(phoneNumber)
			.code(code)
			.verified(false)
			.build();

		phoneVerificationRepository.save(phoneVerification);

		log.info("Verification code sent to {}: {}", phoneNumber, code);
	}

	// 전화번호 인증 코드 생성
	private String generateVerificationCode() {
		return String.valueOf((int)(Math.random() * 900000) + 100000); // 6자리 랜덤 숫자 생성
	}

	// 전화번호 인증 코드 확인
	public boolean verifyPhoneNumber(String phoneNumber, String code) {
		PhoneVerification verification = phoneVerificationRepository.findByPhoneNumber(phoneNumber)
			.orElseThrow(() -> new CustomException(ErrorCode.PHONE_NUMBER_NOT_FOUND));

		if (!verification.getCode().equals(code)) {
			return false; // 인증 코드 불일치
		}

		// 인증 성공 시 verified 상태 업데이트
		verification.setVerified(true);
		phoneVerificationRepository.save(verification);
		return true; // 인증 성공
	}
}
