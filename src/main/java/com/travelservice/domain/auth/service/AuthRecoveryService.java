package com.travelservice.domain.auth.service;

import org.springframework.stereotype.Service;

import com.travelservice.domain.auth.dto.FindEmailRequestDto;
import com.travelservice.domain.auth.entity.PhoneVerification;
import com.travelservice.domain.auth.repository.PhoneVerificationRepository;
import com.travelservice.domain.user.entity.User;
import com.travelservice.domain.user.repository.UserRepository;
import com.travelservice.global.common.exception.CustomException;
import com.travelservice.global.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthRecoveryService {

	private final PhoneVerificationRepository phoneVerificationRepository;
	private final UserRepository userRepository;

	public String findEmail(FindEmailRequestDto requestDto) {
		// 전화번호 인증 코드 확인
		PhoneVerification verification = phoneVerificationRepository.findByPhoneNumber(requestDto.getPhoneNumber())
			.orElseThrow(() -> new CustomException(ErrorCode.PHONE_NUMBER_NOT_FOUND));

		if (!verification.isVerified() || !verification.getCode().equals(requestDto.getCode())) {
			throw new CustomException(ErrorCode.INVALID_AUTH_CODE);
		}

		// 이름과 전화번호로 사용자 검색
		User user = userRepository.findByNameAndPhoneNumber(requestDto.getName(), requestDto.getPhoneNumber())
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		return user.getEmail(); // 이메일 반환
	}
}
