package com.travelservice.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.travelservice.domain.auth.dto.FindEmailRequestDto;
import com.travelservice.domain.auth.dto.ResetPasswordRequestDto;
import com.travelservice.domain.auth.entity.PhoneVerification;
import com.travelservice.domain.auth.repository.PhoneVerificationRepository;
import com.travelservice.domain.user.entity.User;
import com.travelservice.domain.user.repository.UserRepository;
import com.travelservice.global.common.exception.CustomException;
import com.travelservice.global.common.exception.ErrorCode;
import com.travelservice.global.util.TemporaryPasswordGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthRecoveryService {

	private final PhoneVerificationRepository phoneVerificationRepository;
	private final UserRepository userRepository;

	private final MailSender mailSender; // 임시 비밀번호 발송을 위한 메일 서비스
	private final PasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 인코더

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

	public void resetPassword(ResetPasswordRequestDto requestDto) {
		// 1. 사용자 조회
		User user = userRepository.findByEmail(requestDto.getEmail())
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		// 2. 임시 비밀번호 생성
		String tempPassword = TemporaryPasswordGenerator.generateTemporaryPassword();

		// 3. 암호화 후 저장
		user.updatePassword(passwordEncoder.encode(tempPassword));
		userRepository.save(user);

		// 4. 이메일 발송
		mailSender.sendTemporaryPassword(user.getEmail(), tempPassword);

	}
}
