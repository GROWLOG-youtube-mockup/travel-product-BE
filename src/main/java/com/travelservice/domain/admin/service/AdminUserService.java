package com.travelservice.domain.admin.service;

import static com.travelservice.global.common.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travelservice.domain.admin.dto.user.PagedUserResponseDto;
import com.travelservice.domain.admin.dto.user.UserResponseDto;
import com.travelservice.domain.admin.dto.user.UserUpdateRequestDto;
import com.travelservice.domain.admin.entity.AdminActionLog;
import com.travelservice.domain.admin.repository.AdminActionLogRepository;
import com.travelservice.domain.admin.repository.AdminUserRepository;
import com.travelservice.domain.user.entity.User;
import com.travelservice.global.common.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserService {

	private final AdminUserRepository userRepository;
	private final AdminActionLogRepository adminActionLogRepository;

	public PagedUserResponseDto getUsers(Integer page, Integer size, Integer roleCode) {
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
		Page<User> userPage;

		if (roleCode == null) {
			userPage = userRepository.findActiveUsers(pageable);
		} else {
			userPage = userRepository.findActiveUsersByRoleCodes(roleCode, pageable);
		}

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
	public Map<String, Object> updateUser(Long userId, UserUpdateRequestDto requestDto) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Long currentUserId = Long.valueOf(authentication.getName());
		User currentUser = userRepository.findById(currentUserId)
			.orElseThrow(() -> new CustomException(AUTH_INFO_NOT_FOUND));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(USER_NOT_FOUND));

		boolean changed = false;

		// 바꿀 값만 set (null 아닌 값만 반영)
		if (requestDto.getName() != null) {
			user.setName(requestDto.getName());
			changed = true;
		}
		if (requestDto.getEmail() != null) {
			user.setEmail(requestDto.getEmail());
			changed = true;
		}
		if (requestDto.getPhoneNumber() != null) {
			user.setPhoneNumber(requestDto.getPhoneNumber());
			changed = true;
		}
		if (requestDto.getRoleCode() != null) {
			if (currentUser.getRoleCode() == 2) {
				user.setRoleCode(requestDto.getRoleCode());
				changed = true;
			} else {
				throw new RuntimeException("관리자 권한으로는 회원 역할(role_code) 변경이 불가합니다.");
			}
		}

		Map<String, Object> result = new HashMap<>();
		result.put("user_id", user.getUserId());
		result.put("updated_at", user.getUpdatedAt());

		// action-log INSERT
		if (changed) {
			AdminActionLog log = AdminActionLog.builder()
				.user(currentUser)
				.actionType(2) // 2: 사용자 관리
				.targetId(userId)
				.timestamp(LocalDateTime.now())
				.build();
			adminActionLogRepository.save(log);
		}

		return result;
	}

	@Transactional
	public boolean deleteUser(Long userId) {
		int updated = userRepository.softDeleteById(userId);

		if (updated > 0) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Long adminUserId = Long.valueOf(authentication.getName());
			User adminUser = userRepository.findById(adminUserId)
				.orElseThrow(() -> new CustomException(AUTH_INFO_NOT_FOUND));

			AdminActionLog log = AdminActionLog.builder()
				.user(adminUser)
				.actionType(2) // 2: 사용자 관리
				.targetId(userId)
				.timestamp(LocalDateTime.now())
				.build();
			adminActionLogRepository.save(log);
		}

		return updated > 0;
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
