package com.travelservice.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.travelservice.domain.auth.dto.LoginRequestDto;
import com.travelservice.domain.auth.dto.LoginResponseDto;
import com.travelservice.domain.user.entity.User;
import com.travelservice.domain.user.repository.UserRepository;
import com.travelservice.global.common.exception.CustomException;
import com.travelservice.global.common.exception.ErrorCode;
import com.travelservice.global.common.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final PasswordEncoder passwordEncoder;

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
