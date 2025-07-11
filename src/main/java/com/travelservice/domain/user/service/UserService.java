package com.travelservice.domain.user.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.travelservice.domain.auth.repository.EmailVerificationRepository;
import com.travelservice.domain.auth.repository.PhoneVerificationRepository;
import com.travelservice.domain.order.entity.OrderItem;
import com.travelservice.domain.order.repository.OrderItemRepository;
import com.travelservice.domain.user.dto.TripDto;
import com.travelservice.domain.user.dto.UserInfoDto;
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

	@Autowired
	private OrderItemRepository orderItemRepository;

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
		user.setName(newName);
		userRepository.save(user);
	}

	public void updatePhoneNumber(String phoneNumber, Authentication auth) {
		User user = getUserFromAuth(auth);

		// 전화번호 중복 체크(단순히 전화번호가 DB에 존재하는지 확인)
		if (!user.getPhoneNumber().equals(phoneNumber)
			&& userRepository.existsByPhoneNumber(phoneNumber)) {
			throw new CustomException(ErrorCode.PHONE_NUMBER_CONFLICT);
		}

		user.setPhoneNumber(phoneNumber);
		userRepository.save(user);
	}

	public void updatePassword(String currentPassword, String newPassword, Authentication auth) {
		User user = getUserFromAuth(auth);
		if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
			throw new CustomException(ErrorCode.INVALID_PASSWORD);
		}
		user.setPassword(passwordEncoder.encode(newPassword));
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

		// 사용자 삭제 처리
		user.setDeletedAt(LocalDateTime.now());
		userRepository.save(user);
	}

	private User getUserFromAuth(Authentication auth) {
		Long userId = Long.parseLong(auth.getName());
		return userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}

	public UserInfoDto getMyInfo(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		return UserInfoDto.from(user);
	}

	public List<TripDto> getMyTrips(Long userId) {
		List<OrderItem> items = orderItemRepository.findByOrder_User_userId(userId);
		return items.stream()
			.map(TripDto::from)
			.toList();
	}
}
