package com.travelservice.domain.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travelservice.domain.admin.dto.PagedUserResponseDto;
import com.travelservice.domain.admin.dto.UserResponseDto;
import com.travelservice.domain.admin.repository.UserRepository;
import com.travelservice.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;

	public PagedUserResponseDto getAllUsers(int page, int size, Integer roleCode) {
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createAt").descending());

		Page<User> userPage;
		userPage = userRepository.findAllActiveUsers(pageable);

		return PagedUserResponseDto.builder()
			.content(userPage.getContent().stream().map(this::convertToDto).toList())
			.totalElements((int)userPage.getTotalElements())
			.totalPages(userPage.getTotalPages())
			.currentPage(page)
			.build();
	}

	private UserResponseDto convertToDto(User user) {
		return UserResponseDto.builder()
			.userId(Math.toIntExact(user.getUserId()))
			.name(user.getName())
			.email(user.getEmail())
			.phoneNumber(Integer.valueOf(user.getPhoneNumber()))
			.roleCode(user.getRoleCode())
			.createAt(user.getCreatedAt())
			.deleteAt(user.getDeletedAt())
			.build();
	}
}
