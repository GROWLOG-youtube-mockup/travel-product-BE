package com.travelservice.domain.user.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.travelservice.domain.auth.repository.EmailVerificationRepository;
import com.travelservice.domain.auth.repository.PhoneVerificationRepository;
import com.travelservice.domain.user.dto.UserRegistrationRequestDto;
import com.travelservice.domain.user.entity.User;
import com.travelservice.domain.user.repository.UserRepository;
import com.travelservice.global.common.exception.CustomException;
import com.travelservice.global.common.exception.ErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailVerificationRepository emailVerificationRepository;

	@Autowired
	private PhoneVerificationRepository phoneVerificationRepository;

	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public User registerMember(UserRegistrationRequestDto requestDto) {

		//email 중복 체크
		if (userRepository.existsByEmail(requestDto.getEmail())) {
			throw new CustomException(ErrorCode.EMAIL_CONFLICT);
		}

		//전화번호 중복 체크
		if (userRepository.existsByPhoneNumber(requestDto.getPhoneNumber())) {
			throw new CustomException(ErrorCode.PHONE_NUMBER_CONFLICT);
		}

		//인증 확인
		if (!emailVerificationRepository.existsByEmailAndVerifiedTrue(requestDto.getEmail())) {
			throw new CustomException(ErrorCode.EMAIL_NOT_FOUND);
		}

		// 전화번호 인증 확인 - 조건 변경으로 휴대폰 인증 기능은 현재 사용하지 않음
		/*if (!phoneVerificationRepository.existsByPhoneNumberAndVerifiedTrue(requestDto.getPhoneNumber())) {
			throw new CustomException(ErrorCode.PHONE_NUMBER_NOT_FOUND);
		}*/

		User user = User.builder()
			.name(requestDto.getUsername())
			.phoneNumber(requestDto.getPhoneNumber())
			.email(requestDto.getEmail())
			.password(passwordEncoder.encode(requestDto.getPassword())) // 비밀번호 암호화
			.roleCode(0) // 기본 사용자 역할
			.createdAt(LocalDateTime.now())
			.build();
		return userRepository.save(user);
	}

	public void updateName(String newName, Authentication auth) {
		User user = getUserFromAuth(auth);
		user.updateName(newName);
		userRepository.save(user);
	}

	public void updatePhoneNumber(String phoneNumber, Authentication auth) {
		User user = getUserFromAuth(auth);
		user.updatePhoneNumber(phoneNumber);
		userRepository.save(user);
	}

	public void updatePassword(String currentPassword, String newPassword, Authentication auth) {
		User user = getUserFromAuth(auth);
		if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
			throw new CustomException(ErrorCode.INVALID_PASSWORD);
		}
		user.updatePassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}

	public boolean verifyPassword(String password, Authentication auth) {
		User user = getUserFromAuth(auth);
		return passwordEncoder.matches(password, user.getPassword());
	}

	public void deleteAccount(String password, Authentication auth) {
		User user = getUserFromAuth(auth);
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new CustomException(ErrorCode.INVALID_PASSWORD);
		}
	}

	private User getUserFromAuth(Authentication auth) {
		String email = auth.getName();
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

	}
}
