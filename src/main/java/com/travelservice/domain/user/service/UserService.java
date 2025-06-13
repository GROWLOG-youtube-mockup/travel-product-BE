package com.travelservice.domain.user.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.travelservice.domain.user.dto.UserRegistrationRequestDto;
import com.travelservice.domain.user.entity.User;
import com.travelservice.domain.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;

	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public User registerMember(UserRegistrationRequestDto requestDto) {

		//email 중복 체크

		/*if (userRepository.existsByEmail(requestDto.getEmail())) {
			throw new CustomException(ErrorCode.EMAIL_CONFLICT);
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
}
