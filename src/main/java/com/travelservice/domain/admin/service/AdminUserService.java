package com.travelservice.domain.admin.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travelservice.domain.admin.dto.AdminUserResponseDto;
import com.travelservice.domain.admin.dto.PagedAdminUserResponseDto;
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

	public PagedUserResponseDto getUsers(Integer page, Integer size) {
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
		Page<User> userPage;

		userPage = userRepository.findActiveUsers(pageable);

		List<UserResponseDto> users = userPage.getContent().stream()
			.map(this::convertToDto)
			.toList();

		return PagedUserResponseDto.builder()
			.content(users)
			.totalElements(userPage.getTotalElements())
			.totalPages(userPage.getTotalPages())
			.currentPage(page)
			.build();
	}

	@Transactional
	public boolean updateUser(Long userId, UserUpdateRequestDto requestDto) {

		User user = userRepository.findById(userId)
			.orElse(null);
		if (user == null) {
			return false;
		}

		// 바꿀 값만 set (null 아닌 값만 반영)
		if (requestDto.getName() != null) {
			user.setName(requestDto.getName());
		}
		if (requestDto.getEmail() != null) {
			user.setEmail(requestDto.getEmail());
		}
		if (requestDto.getPhoneNumber() != null) {
			user.setPhoneNumber(requestDto.getPhoneNumber());
		}
		if (requestDto.getRoleCode() != null) {
			user.setRoleCode(requestDto.getRoleCode());
		}

		return true;
	}

	@Transactional
	public boolean deleteUser(Long userId) {
		int updated = userRepository.softDeleteById(userId);
		return updated > 0;
	}

	public PagedAdminUserResponseDto getAdminUsers(int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
		List<Integer> adminRoles = List.of(1, 2);
		Page<User> adminUserPage = userRepository.findActiveUsersByRoleCodes(adminRoles, pageable);

		List<AdminUserResponseDto> adminUsers = adminUserPage.getContent().stream()
			.map(this::convertToAdminUserDto)
			.toList();

		return PagedAdminUserResponseDto.builder()
			.content(adminUsers)
			.totalElements(adminUserPage.getTotalElements())
			.totalPages(adminUserPage.getTotalPages())
			.currentPage(page)
			.build();
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

	private AdminUserResponseDto convertToAdminUserDto(User user) {
		return AdminUserResponseDto.builder()
			.userId(user.getUserId())
			.name(user.getName())
			.email(user.getEmail())
			.roleCode(user.getRoleCode())
			.build();
	}
}
