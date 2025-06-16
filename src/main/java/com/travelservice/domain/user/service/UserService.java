package com.travelservice.domain.user.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.travelservice.domain.auth.repository.EmailVerificationRepository;
import com.travelservice.domain.auth.repository.PhoneVerificationRepository;
import com.travelservice.domain.user.dto.LoginRequestDto;
import com.travelservice.domain.user.dto.LoginResponseDto;
import com.travelservice.domain.user.dto.UserRegistrationRequestDto;
import com.travelservice.domain.user.entity.User;
import com.travelservice.domain.user.repository.UserRepository;
import com.travelservice.global.common.exception.CustomException;
import com.travelservice.global.common.exception.ErrorCode;
import com.travelservice.global.common.jwt.JwtTokenProvider;

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
	@Autowired
	private JwtTokenProvider jwtTokenProvider;

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

		if (!phoneVerificationRepository.existsByPhoneNumberAndVerifiedTrue(requestDto.getPhoneNumber())) {
			throw new CustomException(ErrorCode.PHONE_NUMBER_NOT_FOUND);
		}

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

	//로그인
	public LoginResponseDto login(LoginRequestDto requestDto) {
		User user = userRepository.findByEmail(requestDto.getEmail())
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
			throw new CustomException(ErrorCode.LOGIN_FAILED);
		}

		String accessToken = jwtTokenProvider.creteToken(user.getUserId(), user.getRoleCode());

		return new LoginResponseDto(user.getUserId(), user.getName(), accessToken);
	}

	// 로그인한 사용자 정보 조회용
	public User getUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}
}
