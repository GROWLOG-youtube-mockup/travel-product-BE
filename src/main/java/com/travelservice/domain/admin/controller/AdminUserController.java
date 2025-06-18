package com.travelservice.domain.admin.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.travelservice.domain.admin.dto.PagedAdminUserResponseDto;
import com.travelservice.domain.admin.dto.PagedUserResponseDto;
import com.travelservice.domain.admin.dto.UserUpdateRequestDto;
import com.travelservice.domain.admin.service.AdminUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "사용자 관리")
public class AdminUserController {
	private final AdminUserService userService;

	/**
	 * 사용자 목록 조회
	 * @param page 페이지 번호
	 * @param size 페이지 크기
	 * @param roleCode 역할 코드
	 * @return 페이징된 사용자 목록
	 */
	@GetMapping
	@Operation(summary = "사용자 목록 조회")
	public ResponseEntity<PagedUserResponseDto> getUsers(
		@RequestParam(defaultValue = "1") Integer page,
		@RequestParam(defaultValue = "10") Integer size,
		@RequestParam(name = "role_code", required = false) Integer roleCode) {

		// roleCode 유효성 검증 (0, 1, 2 중 하나여야 함)
		if (roleCode != null && (roleCode < 0 || roleCode > 2)) {
			throw new IllegalArgumentException("유효하지 않은 role_code입니다. (0: USER, 1: ADMIN, 2: SUPER_ADMIN)");
		}

		PagedUserResponseDto response = userService.getUsers(page, size, roleCode);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/{userId}")
	@Operation(summary = "사용자 정보 수정")
	public ResponseEntity<Void> updateUser(
		@PathVariable Long userId,
		@RequestBody @Valid UserUpdateRequestDto requestDto
	) {
		boolean updated = userService.updateUser(userId, requestDto);
		if (updated) {
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{userId}")
	@Operation(summary = "회원 삭제")
	public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
		boolean deleted = userService.deleteUser(userId);

		if (deleted) {
			return ResponseEntity.ok().body(Map.of("message", "User deleted (soft delete) successfully"));
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/admins")
	@Operation(summary = "관리자 목록 조회")
	public ResponseEntity<PagedAdminUserResponseDto> getAdmins(
		@RequestParam(defaultValue = "1") Integer page,
		@RequestParam(defaultValue = "10") Integer size
	) {
		PagedAdminUserResponseDto adminUsers = userService.getAdminUsers(page, size);
		return ResponseEntity.ok(adminUsers);
	}
}
