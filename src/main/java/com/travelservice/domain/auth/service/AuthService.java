package com.travelservice.domain.auth.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.travelservice.domain.auth.dto.LoginRequestDto;
import com.travelservice.domain.auth.dto.LoginResponseDto;
import com.travelservice.domain.user.entity.User;
import com.travelservice.domain.user.entity.UserLoginHistory;
import com.travelservice.domain.user.repository.UserLoginHistoryRepository;
import com.travelservice.domain.user.repository.UserRepository;
import com.travelservice.global.common.exception.CustomException;
import com.travelservice.global.common.exception.ErrorCode;
import com.travelservice.global.common.jwt.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final PasswordEncoder passwordEncoder;
	private final UserLoginHistoryRepository userLoginHistoryRepository;

	//로그인
	public LoginResponseDto login(LoginRequestDto requestDto, HttpServletRequest request) {
		User user = userRepository.findByEmail(requestDto.getEmail())
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		// 사용자 삭제 여부 확인(deletedAt이 null이 아닌 경우)
		if (user.getDeletedAt() != null) {
			throw new CustomException(ErrorCode.DELETED_USER); // 아래 ErrorCode 추가 필요
		}

		if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
			throw new CustomException(ErrorCode.LOGIN_FAILED);
		}

		// 로그인 성공 시 사용자 로그인 기록 저장
		String ip = request.getRemoteAddr(); // 클라이언트 IP 주소
		String userAgent = request.getHeader("User-Agent"); // 클라이언트 User-Agent 정보

		UserLoginHistory loginHistory = UserLoginHistory.builder()
			.user(user)
			.loginTime(LocalDateTime.now())
			.ipAddress(ip)
			.userAgent(userAgent)
			.build();

		userLoginHistoryRepository.save(loginHistory);

		String accessToken = jwtTokenProvider.creteToken(user.getUserId(), user.getRoleCode());

		return new LoginResponseDto(user.getUserId(), user.getName(), accessToken);
	}

	// 로그인한 사용자 정보 조회용
	public User getUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}
}
