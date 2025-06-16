package com.travelservice.domain.admin.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travelservice.domain.admin.dto.PagedUserResponseDto;
import com.travelservice.domain.admin.dto.UserResponseDto;
import com.travelservice.domain.admin.dto.UserUpdateRequestDto;
import com.travelservice.domain.admin.repository.AdminUserRepository;
import com.travelservice.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserService {

	private final AdminUserRepository userRepository;

	public PagedUserResponseDto getUsers(Integer page, Integer size, Integer roleCode) {
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

		Page<User> userPage;

		// roleCode가 null이면 전체 조회, 있으면 해당 role만 조회
		if (roleCode == null) {
			userPage = userRepository.findActiveUsers(pageable);
		} else {
			userPage = userRepository.findActiveUsersByRoleCode(roleCode, pageable);
		}

		List<UserResponseDto> content = userPage.getContent().stream()
			.map(this::convertToDto)
			.toList();

		return PagedUserResponseDto.builder()
			.content(content)
			.totalElements(userPage.getTotalElements())
			.totalPages(userPage.getTotalPages())
			.currentPage(page)
			.build();
	}

	@Transactional
	public boolean updateUser(Long userId, UserUpdateRequestDto requestDto) {
		// 1. 엔티티 조회
		User user = userRepository.findById(userId)
			.orElse(null);
		if (user == null)
			return false;

		// 2. 바꿀 값만 set (null 아닌 값만 반영)
		if (requestDto.getName() != null)
			user.setName(requestDto.getName());
		if (requestDto.getEmail() != null)
			user.setEmail(requestDto.getEmail());
		if (requestDto.getPhoneNumber() != null)
			user.setPhoneNumber(requestDto.getPhoneNumber());
		if (requestDto.getRoleCode() != null)
			user.setRoleCode(requestDto.getRoleCode());

		// 3. (JPA가 트랜잭션 종료 시 자동 update)
		return true;
	}

	private UserResponseDto convertToDto(User user) {
		return UserResponseDto.builder()
			.userId(user.getUserId())
			.name(user.getName())
			.email(user.getEmail())
			.phoneNumber(user.getPhoneNumber())
			.roleCode(user.getRoleCode())
			.createAt(user.getCreatedAt())
			.deleteAt(user.getDeletedAt())
			.build();
	}
}
